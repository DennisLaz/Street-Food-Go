package gr.hua.dit.StreetFoodGo.core.service.model;

import gr.hua.dit.StreetFoodGo.core.model.OrderItem;
import jakarta.validation.constraints.*;

import java.util.List;

public record OpenOrderRequest(
        @NotNull @Positive Long customerId,
        @NotNull @Positive Long restaurantId,
        @NotNull @NotBlank List<@Size(max=1000) OrderItemView> orderItems
) {
}
