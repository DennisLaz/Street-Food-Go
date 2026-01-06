package gr.hua.dit.StreetFoodGo.core.service.model;

import java.time.Instant;
import java.util.List;

/**
 * General view of {@link gr.hua.dit.StreetFoodGo.core.model.Menu}
 *
 * @see gr.hua.dit.StreetFoodGo.core.model.Menu
 */
public record MenuView(
        long menuId,
        PersonView restaurant,
        String title,
        boolean active,
        Instant createdAt,
        List<MenuItemView> items
) {
}
