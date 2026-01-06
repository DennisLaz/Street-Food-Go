package gr.hua.dit.StreetFoodGo.web.ui.model;

import java.util.List;

public record MenuPublicView(
        String title,
        String restaurantName,
        List<MenuItemPublicView> items
) {}
