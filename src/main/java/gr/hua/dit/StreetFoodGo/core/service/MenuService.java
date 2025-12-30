package gr.hua.dit.StreetFoodGo.core.service;

import gr.hua.dit.StreetFoodGo.core.model.Menu;
import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuCreateRequest;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuPublicView;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuUpdateRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.MenuView;
import jakarta.validation.Valid;

import java.util.Optional;

public interface MenuService {

    Optional<MenuView> getActiveMenuByRestaurant(Long restaurantId);

    MenuView createMenu(@Valid MenuCreateRequest createMenuDto);

    MenuView updateMenu(Long menuId, @Valid MenuUpdateRequest updateMenuDto);

    void deactivateMenu(Long menuId);

    Optional<MenuView> getMyMenu();

    Optional<MenuView> getMenuByRestaurant(Long restaurantId);

    MenuView createMenuForRestaurant(Person restaurant, MenuCreateRequest request);

    Optional<MenuPublicView> getPublicMenuByRestaurant(Long restaurantId);

}
