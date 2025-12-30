package gr.hua.dit.StreetFoodGo.core.service.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StartOrderRequest(
        @NotNull @Positive Long orderId
) {
}
