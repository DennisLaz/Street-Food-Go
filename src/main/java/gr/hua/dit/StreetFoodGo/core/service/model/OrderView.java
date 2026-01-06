package gr.hua.dit.StreetFoodGo.core.service.model;

import gr.hua.dit.StreetFoodGo.core.model.OrderItem;
import gr.hua.dit.StreetFoodGo.core.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * General view of {@link gr.hua.dit.StreetFoodGo.core.model.Order} entity
 *
 * @see gr.hua.dit.StreetFoodGo.core.model.Order
 * @see gr.hua.dit.StreetFoodGo.core.service.OrderService
 *
 */
public record OrderView(
        long orderId,
        PersonView customer,
        PersonView restaurant,
        OrderStatus status,
        Instant queuedAt,
        Instant inProgressAt,
        Instant completedAt,
        List<OrderItemView>items,
        BigDecimal totalPrice
) {

}