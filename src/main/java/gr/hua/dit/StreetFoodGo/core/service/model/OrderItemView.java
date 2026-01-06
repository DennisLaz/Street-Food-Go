package gr.hua.dit.StreetFoodGo.core.service.model;

import java.math.BigDecimal;

public record OrderItemView(
        long orderItemId,
        long menuItemId,
        String name,
        BigDecimal price,
        int quantity,
        BigDecimal totalPrice
) {}

