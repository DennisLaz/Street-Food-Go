package gr.hua.dit.StreetFoodGo.core.service.mapper;

import gr.hua.dit.StreetFoodGo.core.model.Menu;
import gr.hua.dit.StreetFoodGo.core.model.MenuItem;
import gr.hua.dit.StreetFoodGo.core.service.model.MenuItemView;
import gr.hua.dit.StreetFoodGo.core.service.model.MenuView;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuEditForm;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuItemUpdateForm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert {@link Menu} to {@link MenuView}
 */
@Component
public class MenuMapper {

    private final PersonMapper personMapper;

    public MenuMapper(PersonMapper personMapper) {
        if (personMapper == null) throw new NullPointerException();
        this.personMapper = personMapper;
    }

    public MenuView convertMenuToMenuView(final Menu menu) {
        if (menu == null) return null;

        return new MenuView(
                menu.getMenuId(),
                this.personMapper.convertPersonToPersonView(menu.getRestaurant()),
                menu.getTitle(),
                menu.isActive(),
                menu.getCreatedAt(),
                convertMenuItems(menu.getItems())
        );
    }

    private List<MenuItemView> convertMenuItems(final List<MenuItem> items) {
        if (items == null) return List.of();

        return items.stream()
                .map(this::convertMenuItemToMenuItemView)
                .collect(Collectors.toList());
    }

    private MenuItemView convertMenuItemToMenuItemView(final MenuItem item) {
        if (item == null) return null;

        return new MenuItemView(
                item.getMenuItemId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.isAvailable()
        );
    }

    public MenuEditForm toEditForm(MenuView menu) {

        MenuEditForm form = new MenuEditForm();
        form.setTitle(menu.title());

        List<MenuItemUpdateForm> items = menu.items().stream()
                .map(item -> new MenuItemUpdateForm(
                        item.menuItemId(),   // ΠΡΕΠΕΙ να υπάρχει στο MenuItemView
                        item.name(),
                        item.price(),
                        item.description(),
                        false                // default delete = false
                ))
                .toList();

        form.setItems(items);
        return form;
    }
}
