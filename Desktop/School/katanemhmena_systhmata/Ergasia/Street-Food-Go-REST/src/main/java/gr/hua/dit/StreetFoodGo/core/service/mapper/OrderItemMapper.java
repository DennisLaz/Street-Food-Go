package gr.hua.dit.StreetFoodGo.core.service.mapper;

import gr.hua.dit.StreetFoodGo.core.model.OrderItem;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderItemView;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert {@link OrderItem} to {@link OrderItemView}
 */
@Component
public class OrderItemMapper {

    public OrderItemView convertOrderItemToOrderItemView(final OrderItem orderItem) {
        if (orderItem == null) return null;

        return new OrderItemView(
                orderItem.getId(),
                orderItem.getMenuItem().getMenuItemId(),
                orderItem.getMenuItem().getName(),
                orderItem.getMenuItem().getPrice(),
                orderItem.getQuantity(),
                orderItem.getTotalPrice()
        );
    }
}
