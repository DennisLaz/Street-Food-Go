package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;

public record MenuItemRequest(

        @NotBlank
        String name,

        double price,

        String description
) {
}
