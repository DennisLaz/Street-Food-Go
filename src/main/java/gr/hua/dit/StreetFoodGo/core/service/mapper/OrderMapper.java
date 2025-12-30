package gr.hua.dit.StreetFoodGo.core.service.mapper;

import gr.hua.dit.StreetFoodGo.core.model.Order;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert {@link gr.hua.dit.StreetFoodGo.core.model.Order} to {@link gr.hua.dit.StreetFoodGo.core.service.model.OrderView}
 */
@Component
public class OrderMapper {

    private final PersonMapper personMapper;

    public OrderMapper(PersonMapper personMapper) {
        if (personMapper == null) throw new NullPointerException();
        this.personMapper = personMapper;
    }

    public OrderView convertOrdertoOrderView(final Order order) {
        if (order == null) return null;
        return new OrderView(
                order.getOrderId(),
                this.personMapper.convertPersonToPersonView(order.getCustomer()),
                this.personMapper.convertPersonToPersonView(order.getRestaurant()),
                order.getStatus(),
                order.getSubject(),
                order.getCustomerContent(),
                order.getRestaurantContent(),
                order.getQueuedAt(),
                order.getInProgressAt(),
                order.getCompletedAt()
        );
    }
}
