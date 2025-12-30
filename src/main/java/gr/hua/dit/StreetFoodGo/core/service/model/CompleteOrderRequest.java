package gr.hua.dit.StreetFoodGo.core.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CompleteOrderRequest(@NotNull @Positive Long orderId,
                                   @NotNull @NotBlank @Size(max=1000) String restauranContent) {
}
