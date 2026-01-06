package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a Menu
 */
public record CreateMenuDto(

        @NotNull
        @NotBlank
        @Size(min = 1, max = 255)
        String title

) {
}
