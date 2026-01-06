package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating a Menu
 */
public record UpdateMenuDto(

        @NotBlank
        @Size(min = 1, max = 255)
        String title,

        Boolean active

) {
}

