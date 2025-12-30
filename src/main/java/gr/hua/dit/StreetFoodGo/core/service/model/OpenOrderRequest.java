package gr.hua.dit.StreetFoodGo.core.service.model;

import jakarta.validation.constraints.*;

public record OpenOrderRequest(
        @NotNull @Positive Long customerId,
        @NotNull @Positive Long restaurantId,
        @NotNull @NotBlank @Size(max=255) String subject,
        @NotNull @NotBlank @Size(max=1000) String customerContent
) {
}
