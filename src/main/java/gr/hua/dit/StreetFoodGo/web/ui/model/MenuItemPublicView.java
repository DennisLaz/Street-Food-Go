package gr.hua.dit.StreetFoodGo.web.ui.model;

import java.math.BigDecimal;

public record MenuItemPublicView(
        Long menuItemId,
        String name,
        String description,
        BigDecimal price
) {
}

