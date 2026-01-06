package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MenuCreateRequest(

        @NotBlank
        @Size(max = 255)
        String title,

        @NotEmpty
        List<MenuItemRequest> items
) {
}

