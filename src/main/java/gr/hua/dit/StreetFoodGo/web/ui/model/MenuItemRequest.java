package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record MenuItemRequest(

        @NotBlank
        String name,

        BigDecimal price,

        String description
) {
}
