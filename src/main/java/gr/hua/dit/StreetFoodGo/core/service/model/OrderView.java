package gr.hua.dit.StreetFoodGo.core.service.model;

import gr.hua.dit.StreetFoodGo.core.model.OrderStatus;
import java.time.Instant;

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
        String subject,
        String customerContent,
        String restaurantContent,
        Instant queuedAt,
        Instant inProgressAt,
        Instant completedAt
) {

}