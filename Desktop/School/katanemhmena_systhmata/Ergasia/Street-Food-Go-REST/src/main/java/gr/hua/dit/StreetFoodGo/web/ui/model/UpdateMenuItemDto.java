package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateMenuItemDto(

        @Size(min = 1, max = 255)
        String name,

        @Size(max = 1000)
        String description,

        BigDecimal price,

        Boolean available

) {
}

