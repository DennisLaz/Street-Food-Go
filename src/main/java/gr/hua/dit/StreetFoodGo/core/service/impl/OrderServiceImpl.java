package gr.hua.dit.StreetFoodGo.core.service.impl;

import gr.hua.dit.StreetFoodGo.core.model.Order;
import gr.hua.dit.StreetFoodGo.core.model.OrderStatus;
import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.port.SmsNotificationPort;
import gr.hua.dit.StreetFoodGo.core.repository.OrderRepository;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.OrderService;
import gr.hua.dit.StreetFoodGo.core.service.mapper.OrderMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.CompleteOrderRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OpenOrderRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
import gr.hua.dit.StreetFoodGo.core.service.model.StartOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Default Implementatuion of {@link OrderService}
 *
 * TODO some parts can be reused(security checks)
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final Set<OrderStatus> ACTIVE = Set.of(OrderStatus.QUEUED, OrderStatus.INPROGRESS);

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final CurrentUserProvider currentUserProvider;
    private final SmsNotificationPort smsNotificationPort;



    public OrderServiceImpl(OrderMapper orderMapper,
                            OrderRepository orderRepository,
                            PersonRepository personRepository,
                            CurrentUserProvider currentUserProvider,
                            SmsNotificationPort smsNotificationPort) {
        if(orderMapper == null)throw new NullPointerException();
        if(orderRepository == null)throw new NullPointerException();
        if(personRepository == null)throw new NullPointerException();
        if(currentUserProvider == null)throw new NullPointerException();
        if(smsNotificationPort == null)throw new NullPointerException();

        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.personRepository = personRepository;
        this.currentUserProvider = currentUserProvider;
        this.smsNotificationPort = smsNotificationPort;
    }

    private void notifyPerson(final OrderView orderView, final PersonType type) {
        final String e164;
        if(type == PersonType.CUSTOMER){
            e164 = orderView.customer().phoneNumber();
        }else if (type ==PersonType.RESTAURANT){
            e164 = orderView.restaurant().phoneNumber();
        }else {
            throw new RuntimeException("Unreachable");
        }
        final String content = String.format("Order %s new status: %s", orderView.orderId(), orderView.status().name());
        final boolean sent = this.smsNotificationPort.sendSms(e164, content);
        if(!sent){
            LOGGER.warn("SMS to {} has failed to send", orderView.orderId());
        }
    }

    @Override
    public Optional<OrderView> getOrder(Long id) {
        if(id == null)throw new NullPointerException();
        if(id<0)throw new IllegalArgumentException();

        //-------------------------------------------

        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();

        //-------------------------------------------

        final Order order;
        try{
            order = this.orderRepository.getReferenceById(id);
        }catch (EntityNotFoundException ignored){
            return Optional.empty();
        }

        //-------------------------------------------

        //USER MUST HAVE ACCESS TO THE ORDER
        //-------------------------------------------

        final long orderPersonId;
        if(currentUser.type() == PersonType.RESTAURANT){
            orderPersonId = order.getRestaurant().getId();
        }else if(currentUser.type() == PersonType.CUSTOMER){
            orderPersonId = order.getCustomer().getId();
        }else{
            throw new SecurityException("Unsupported role");
        }
        if(currentUser.id() != orderPersonId){
            return Optional.empty();
        }

        final OrderView orderView = this.orderMapper.convertOrdertoOrderView(order);

        //-------------------------------------------

        return Optional.of(orderView);
    }

    @Override
    public List<OrderView> getOrders() {
        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        final List<Order> orderList;
        if(currentUser.type() == PersonType.RESTAURANT){
            orderList = this.orderRepository.findAllByRestaurantId(currentUser.id());
        }else if(currentUser.type() == PersonType.CUSTOMER){
            orderList = this.orderRepository.findAllByCustomerId(currentUser.id());
        }else{
            throw new SecurityException("Unsupported role");
        }

        return orderList.stream().map(this.orderMapper::convertOrdertoOrderView).toList();
    }

    @Override
    public OrderView openOrder(@Valid final OpenOrderRequest openOrderRequest,final boolean notify) {
        if(openOrderRequest == null)throw new NullPointerException();
        //Unpack
        //-------------------------------------------

        final long customerId = openOrderRequest.customerId();
        final long restaurantId = openOrderRequest.restaurantId();
        final String subject = openOrderRequest.subject();
        final String customerContent = openOrderRequest.customerContent();

        //-------------------------------------------

        final Person customer =this.personRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        final Person restaurant=this.personRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        //-------------------------------------------

        if(customer.getType() != PersonType.CUSTOMER){
            throw new IllegalArgumentException("CustomerId must refer to a customer");
        }
        if(restaurant.getType() != PersonType.RESTAURANT){
            throw new IllegalArgumentException("RestaurantId must refer to a restaurant");
        }

        //Security
        //-------------------------------------------

        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        if(currentUser.type() != PersonType.CUSTOMER){
            throw new  IllegalArgumentException("Customer role required");
        }
        if(currentUser.id() != customerId){
            throw new IllegalArgumentException("Authenticated customer does not match the order's customer id");
        }

        //Rules
        //-------------------------------------------

        //Rule 1 : Customer must have only one order active at once
        if(this.orderRepository.existsByCustomerIdAndRestaurantIdAndStatusIn(customerId,restaurantId,ACTIVE));

        //-------------------------------------------

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setCustomerContent(customerContent);
        order.setStatus(OrderStatus.QUEUED);
        order.setSubject(subject);
        order.setQueuedAt(Instant.now());
        order = this.orderRepository.save(order);

        //-------------------------------------------

        final OrderView orderView = this.orderMapper.convertOrdertoOrderView(order);

        //-------------------------------------------
        if(notify) {
            this.notifyPerson(orderView, PersonType.RESTAURANT);
        }
        return orderView;
    }

    @Override
    public OrderView startOrder(final  StartOrderRequest startOrderRequest) {
        if(startOrderRequest == null)throw new NullPointerException();

        //unpack

        final long orderId = startOrderRequest.orderId();

        //----------------------------------------------

        final Order order = this.orderRepository.findById(orderId).
                orElseThrow(() -> new EntityNotFoundException("Order does not exist"));

        //security
        //----------------------------------------------
        final long restaurantId = order.getRestaurant().getId();
        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        if(currentUser.type() != PersonType.RESTAURANT){
            throw new SecurityException("Restaurant role required");
        }
        if(currentUser.id() != restaurantId){
            throw new SecurityException("Authenticated restaurant does not match with the order's restaurant id");
        }

        //rules
        //----------------------------------------------

        if(order.getStatus() != (OrderStatus.QUEUED)){
            throw new IllegalArgumentException("Only QUEUED orders can be started");
        }

        //----------------------------------------------

        order.setStatus(OrderStatus.INPROGRESS);
        order.setInProgressAt(Instant.now());

        final Order savedOrder = this.orderRepository.save(order);

        final OrderView orderView = this.orderMapper.convertOrdertoOrderView(savedOrder);

        //----------------------------------------------

        this.notifyPerson(orderView, PersonType.RESTAURANT);

        return orderView;
    }

    @Override
    public OrderView completeOrder(@Valid final CompleteOrderRequest completeOrderRequest) {

        if(completeOrderRequest == null)throw new NullPointerException();

        //Unpack
        //----------------------------------------------

        final long orderId = completeOrderRequest.orderId();
        final String restaurantContent = completeOrderRequest.restauranContent();

        //----------------------------------------------

        final Order order=this.orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order does not exist"));

        //security
        //----------------------------------------------

        final long restaurantId = order.getRestaurant().getId();
        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        if(currentUser.type() != PersonType.RESTAURANT){
            throw new SecurityException("Restaurant role required");
        }
        if(currentUser.id() != restaurantId){
            throw new SecurityException("Authenticated restaurant does not match the order's restaurant id");
        }

        //----------------------------------------------

        if(order.getStatus() != (OrderStatus.INPROGRESS)){
            throw new IllegalArgumentException("Only INPROGRESS orders can be completed");
        }

        //----------------------------------------------

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(Instant.now());
        order.setRestaurantContent(restaurantContent);

        //----------------------------------------------

        final Order savedOrder = this.orderRepository.save(order);

        //----------------------------------------------

        final OrderView orderView = this.orderMapper.convertOrdertoOrderView(savedOrder);

        //----------------------------------------------

        this.notifyPerson(orderView, PersonType.RESTAURANT);

        return orderView;
    }
}
