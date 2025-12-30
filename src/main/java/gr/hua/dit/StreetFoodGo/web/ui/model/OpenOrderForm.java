package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OpenOrderForm(
        @NotNull @Positive Long restaurantId,
        @NotNull @NotBlank @Size(max=255) String subject,
        @NotNull @NotBlank @Size(max=1000) String customerContent
) {

}
