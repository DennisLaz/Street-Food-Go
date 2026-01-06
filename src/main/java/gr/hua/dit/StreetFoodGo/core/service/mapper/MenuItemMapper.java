package gr.hua.dit.StreetFoodGo.core.service.mapper;

import gr.hua.dit.StreetFoodGo.core.model.MenuItem;
import gr.hua.dit.StreetFoodGo.core.service.model.MenuItemView;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    public MenuItemView toView(MenuItem item) {
        return new MenuItemView(
                item.getMenuItemId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.isAvailable()
        );
    }
}
