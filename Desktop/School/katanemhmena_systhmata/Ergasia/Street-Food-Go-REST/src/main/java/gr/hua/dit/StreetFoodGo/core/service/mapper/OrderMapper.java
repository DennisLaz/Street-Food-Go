package gr.hua.dit.StreetFoodGo.core.service.mapper;

import gr.hua.dit.StreetFoodGo.core.model.Order;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderItemView;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
import org.springframework.stereotype.Component;

import java.util.List;

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
        List<OrderItemView> items =order.getItems().stream()
                .map(item -> new OrderItemView(
                        item.getId(),
                        item.getMenuItem().getMenuItemId(),
                        item.getMenuItem().getName(),
                        item.getMenuItem().getPrice(),
                        item.getQuantity(),
                        item.getTotalPrice()
                ))
                .toList();
        return new OrderView(
                order.getOrderId(),
                this.personMapper.convertPersonToPersonView(order.getCustomer()),
                this.personMapper.convertPersonToPersonView(order.getRestaurant()),
                order.getStatus(),
                order.getQueuedAt(),
                order.getInProgressAt(),
                order.getCompletedAt(),
                items,
                order.getTotalPrice()
        );
    }
}
