package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateMenuItemDto(

        @NotNull
        @NotBlank
        @Size(min = 1, max = 255)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        BigDecimal price

) {
}
