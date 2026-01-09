package gr.hua.dit.StreetFoodGo.core.service.model;

import gr.hua.dit.StreetFoodGo.core.model.OrderStatus;
import gr.hua.dit.StreetFoodGo.core.service.OrderBusinessLogicService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 *
 * General view of {@link gr.hua.dit.StreetFoodGo.core.model.Order} entity
 *
 * @see gr.hua.dit.StreetFoodGo.core.model.Order
 * @see OrderBusinessLogicService
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
) {}