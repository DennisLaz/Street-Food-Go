package gr.hua.dit.StreetFoodGo.core.service.impl;

import gr.hua.dit.StreetFoodGo.core.model.*;
import gr.hua.dit.StreetFoodGo.core.port.SmsNotificationPort;
import gr.hua.dit.StreetFoodGo.core.repository.MenuItemRepository;
import gr.hua.dit.StreetFoodGo.core.repository.OrderRepository;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.OrderBusinessLogicService;
import gr.hua.dit.StreetFoodGo.core.service.mapper.OrderMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Default Implementation of {@link OrderBusinessLogicService}
 */
@Service
public class OrderBusinessLogicServiceImpl implements OrderBusinessLogicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBusinessLogicServiceImpl.class);
    private static final Set<OrderStatus> ACTIVE = Set.of(OrderStatus.OPEN, OrderStatus.QUEUED, OrderStatus.INPROGRESS);

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final CurrentUserProvider currentUserProvider;
    private final SmsNotificationPort smsNotificationPort;
    private final MenuItemRepository menuItemRepository;

    public OrderBusinessLogicServiceImpl(OrderMapper orderMapper,
                            OrderRepository orderRepository,
                            PersonRepository personRepository,
                            CurrentUserProvider currentUserProvider,
                            SmsNotificationPort smsNotificationPort,
                            MenuItemRepository menuItemRepository) {
        if(orderMapper == null)throw new NullPointerException();
        if(orderRepository == null)throw new NullPointerException();
        if(personRepository == null)throw new NullPointerException();
        if(currentUserProvider == null)throw new NullPointerException();
        if(smsNotificationPort == null)throw new NullPointerException();
        if(menuItemRepository == null)throw new NullPointerException();

        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.personRepository = personRepository;
        this.currentUserProvider = currentUserProvider;
        this.smsNotificationPort = smsNotificationPort;
        this.menuItemRepository  = menuItemRepository;
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
    public void addItemToOpenOrder(Long customerId, Long menuItemId) {

        // -------------------------------------------------
        // Load customer
        // -------------------------------------------------
        Person customer = personRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (customer.getType() != PersonType.CUSTOMER) {
            throw new SecurityException("Only customers can place orders");
        }

        // -------------------------------------------------
        // Load menu item
        // -------------------------------------------------
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found"));

        if (!menuItem.isAvailable()) {
            throw new IllegalStateException("Menu item is not available");
        }

        Person restaurant = menuItem.getMenu().getRestaurant();

        // -------------------------------------------------
        // Find or create OPEN order
        // -------------------------------------------------
        Order order = orderRepository
                .findByCustomerIdAndStatus(customerId, OrderStatus.QUEUED)
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setCustomer(customer);
                    newOrder.setRestaurant(restaurant);
                    newOrder.setStatus(OrderStatus.QUEUED);
                    return newOrder;
                });

        // -------------------------------------------------
        // Rule: Customer can only order again if the order is cancelled or completed
        // -------------------------------------------------
        if (!order.getRestaurant().getId().equals(restaurant.getId()) && order.isActive()){


                throw new IllegalStateException(
                        "You already have an open order from another restaurant"
                );

        }


        // -------------------------------------------------
        // Add or update OrderItem
        // -------------------------------------------------
        order.addItem(menuItem);

        // -------------------------------------------------
        // Persist
        // -------------------------------------------------
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderView getOrCreateOpenOrderForCustomer(Long customerId) {

        Person customer = personRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with id: " + customerId
                ));

        return orderRepository
                .findByCustomerIdAndStatusIn(customerId, ACTIVE)

                .map(orderMapper::convertOrdertoOrderView)
                .orElseGet(() -> {

                    Order order = new Order();
                    order.setCustomer(customer);
                    order.setStatus(OrderStatus.OPEN);

                    orderRepository.save(order);

                    return orderMapper.convertOrdertoOrderView(order);
                });
    }

    @Override
    @Transactional
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
    @Transactional
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

        //-------------------------------------------

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.QUEUED);
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
    @Transactional
    public OrderView startOrder(final long orderId) {

        final StartOrderRequest request =
                new StartOrderRequest(orderId);

        return this.startOrder(request);
    }


    @Override
    @Transactional
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

        this.notifyPerson(orderView, PersonType.CUSTOMER);

        return orderView;
    }

    @Override
    @Transactional
    public OrderView completeOrder(final long orderId) {

        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order does not exist"));

        final CompleteOrderRequest request =
                new CompleteOrderRequest(orderId, new ArrayList<>(order.getItems()));

        return this.completeOrder(request);
    }


    @Override
    public OrderView completeOrder(@Valid final CompleteOrderRequest completeOrderRequest) {

        if(completeOrderRequest == null)throw new NullPointerException();

        //Unpack
        //----------------------------------------------

        final long orderId = completeOrderRequest.orderId();
        final ArrayList<OrderItem> orderItems = completeOrderRequest.orderItems();

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

        //----------------------------------------------

        final Order savedOrder = this.orderRepository.save(order);

        //----------------------------------------------

        final OrderView orderView = this.orderMapper.convertOrdertoOrderView(savedOrder);

        //----------------------------------------------

        this.notifyPerson(orderView, PersonType.CUSTOMER);

        return orderView;
    }

    @Override
    @Transactional
    public OrderView acceptOrder(final long orderId) {

        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order does not exist"));

        // security
        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.RESTAURANT) {
            throw new SecurityException("Restaurant role required");
        }
        if (currentUser.id() != order.getRestaurant().getId()) {
            throw new SecurityException("Authenticated restaurant does not match order restaurant");
        }

        // rules
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING orders can be accepted");
        }

        // state change
        order.setStatus(OrderStatus.QUEUED);
        order.setQueuedAt(Instant.now());

        final Order savedOrder = orderRepository.save(order);
        final OrderView orderView = orderMapper.convertOrdertoOrderView(savedOrder);

        notifyPerson(orderView, PersonType.CUSTOMER);

        return orderView;
    }

    @Override
    @Transactional
    public void rejectOrder(final long orderId) {

        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order does not exist"));

        // security
        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.RESTAURANT) {
            throw new SecurityException("Restaurant role required");
        }
        if (currentUser.id() != order.getRestaurant().getId()) {
            throw new SecurityException("Authenticated restaurant does not match order restaurant");
        }

        // rules
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING orders can be rejected");
        }

        // state change
        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        final Order savedOrder = orderRepository.save(order);
        final OrderView orderView = orderMapper.convertOrdertoOrderView(savedOrder);

        notifyPerson(orderView, PersonType.CUSTOMER);
    }


    @Override
    @Transactional
    public void addItemToOpenOrder(
            Long customerId,
            Long restaurantId,
            Long menuItemId
    ) {
        Order order = orderRepository
                .findByCustomerIdAndStatusIn(customerId, ACTIVE)
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setCustomer(personRepository.getReferenceById(customerId));
                    newOrder.setRestaurant(personRepository.getReferenceById(restaurantId));
                    newOrder.setStatus(OrderStatus.OPEN);
                    return orderRepository.save(newOrder);
                });

        // ❗ έλεγχος ότι το order είναι για το ίδιο restaurant
        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new IllegalStateException("Order belongs to another restaurant");
        }

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found"));

        order.addItem(menuItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderView> getOpenOrderForCurrentCustomer(long id ) {

        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new SecurityException("Authentication required"));

        if (user.type() != PersonType.CUSTOMER) {
            throw new SecurityException("Customer role required");
        }

        return orderRepository
                .findOpenByCustomerId(user.id())
                .map(orderMapper::convertOrdertoOrderView);
    }

    @Transactional
    @Override
    public void removeItemFromOpenOrder(Long customerId, Long menuItemId) {
        Order order = orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.OPEN)
                .orElseThrow(() -> new IllegalArgumentException("No open order found"));

        order.removeItem(menuItemId);

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void submitOrder(long customerId) {

        Order order = orderRepository
                .findByCustomerIdAndStatus(customerId, OrderStatus.OPEN)
                .orElseThrow(() ->
                        new IllegalStateException("No open order to submit")
                );

        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot submit empty order");
        }

        order.setStatus(OrderStatus.PENDING);
        order.setQueuedAt(Instant.now());

        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderView> getLatestPendingOrderForCustomer(Long customerId) {

        return orderRepository
                .findFirstByCustomerIdAndStatusOrderByQueuedAtDesc(
                        customerId,
                        OrderStatus.PENDING
                )
                .map(orderMapper::convertOrdertoOrderView);
    }



}
