package gr.hua.dit.StreetFoodGo.web.ui.model;

import java.math.BigDecimal;

public record MenuItemPublicView(
        String name,
        BigDecimal price,
        String description
) {}
