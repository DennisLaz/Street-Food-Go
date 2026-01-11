package gr.hua.dit.StreetFoodGo.web.ui.model;

import java.math.BigDecimal;

public record MenuItemUpdateForm(
        Long id,
        String name,
        BigDecimal price,
        String description,
        boolean delete
) {}

