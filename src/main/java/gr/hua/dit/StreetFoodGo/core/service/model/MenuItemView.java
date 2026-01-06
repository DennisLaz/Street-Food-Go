package gr.hua.dit.StreetFoodGo.core.service.model;


import java.math.BigDecimal;

/**
 * View of {@link gr.hua.dit.StreetFoodGo.core.model.MenuItem}
 */
public record MenuItemView(
        long menuItemId,
        String name,
        String description,
        BigDecimal price,
        boolean available
) {
}

