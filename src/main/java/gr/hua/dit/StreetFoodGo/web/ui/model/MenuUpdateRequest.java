package gr.hua.dit.StreetFoodGo.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MenuUpdateRequest(

        @NotBlank
        @Size(max = 255)
        String title,

        List<MenuItemRequest> items,

        boolean active
) {
}
