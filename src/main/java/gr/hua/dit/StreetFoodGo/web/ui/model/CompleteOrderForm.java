package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompleteOrderForm(
        @NotNull @NotBlank @Size(max=1000) String restaurantContent
) {
}
