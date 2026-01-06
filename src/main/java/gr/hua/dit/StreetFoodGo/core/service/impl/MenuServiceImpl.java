package gr.hua.dit.StreetFoodGo.core.service.impl;

import gr.hua.dit.StreetFoodGo.core.model.Menu;
import gr.hua.dit.StreetFoodGo.core.model.MenuItem;
import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.repository.MenuRepository;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.MenuService;
import gr.hua.dit.StreetFoodGo.web.ui.model.*;
import gr.hua.dit.StreetFoodGo.core.service.mapper.MenuMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.MenuView;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final PersonRepository personRepository;
    private final MenuMapper menuMapper;
    private final CurrentUserProvider currentUserProvider;

    public MenuServiceImpl(MenuRepository menuRepository,
                           PersonRepository personRepository,
                           MenuMapper menuMapper,
                           CurrentUserProvider currentUserProvider) {

        if (menuRepository == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (menuMapper == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();

        this.menuRepository = menuRepository;
        this.personRepository = personRepository;
        this.menuMapper = menuMapper;
        this.currentUserProvider = currentUserProvider;
    }

    private MenuItem toMenuItem(MenuItemRequest request, Menu menu) {
        MenuItem item = new MenuItem();
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setMenu(menu);                  // ⬅️ κρίσιμο
        item.setAvailable(true);
        return item;
    }


    @Override
    public Optional<MenuView> getActiveMenuByRestaurant(Long restaurantId) {
        if (restaurantId == null || restaurantId < 0) {
            throw new IllegalArgumentException();
        }

        return menuRepository
                .findByRestaurantIdAndActiveTrue(restaurantId)
                .map(menuMapper::convertMenuToMenuView);
    }

    @Override
    @Transactional
    public MenuView createMenu(@Valid MenuCreateRequest createMenuDto) {
        if (createMenuDto == null) throw new NullPointerException();

        //-----------------------------------------
        // Security
        //-----------------------------------------
        final CurrentUser currentUser = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new SecurityException("Authentication required"));

        if (currentUser.type() != PersonType.RESTAURANT) {
            throw new SecurityException("Restaurant role required");
        }

        final Long restaurantId = currentUser.id();

        //-----------------------------------------
        // Load restaurant entity
        //-----------------------------------------
        final Person restaurant = personRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        //-----------------------------------------
        // Rule: only one active menu per restaurant
        //-----------------------------------------
        if (menuRepository.existsByRestaurantIdAndActiveTrue(restaurantId)) {
            throw new IllegalStateException("Restaurant already has an active menu");
        }

        //-----------------------------------------
        Menu menu = getMenu(createMenuDto, restaurant);

        final Menu savedMenu = menuRepository.save(menu);

        return menuMapper.convertMenuToMenuView(savedMenu);
    }

    private static Menu getMenu(MenuCreateRequest createMenuDto, Person restaurant) {
        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menu.setTitle(createMenuDto.title());
        menu.setActive(true);

        //-----------------------------------------
        // Create menu items and attach to menu
        for (MenuItemRequest itemRequest : createMenuDto.items()) {
            MenuItem item = new MenuItem();
            item.setName(itemRequest.name());
            item.setDescription(itemRequest.description());
            item.setPrice(itemRequest.price()); // double → BigDecimal
            item.setAvailable(true);

            menu.addItem(item); // σημαντικό: ορίζει το menu στο item και προσθέτει στη λίστα
        }
        return menu;
    }


    @Override
    @Transactional
    public MenuView createMenuForRestaurant(Person restaurant, MenuCreateRequest request) {

        if (restaurant.getType() != PersonType.RESTAURANT) {
            throw new IllegalArgumentException("Person is not a restaurant");
        }

        if (menuRepository.existsByRestaurantIdAndActiveTrue(restaurant.getId())) {
            throw new IllegalStateException("Restaurant already has menu");
        }

        // 1. Δημιουργία Menu
        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menu.setTitle(request.title());
        menu.setActive(true);

        // 2. Δημιουργία MenuItems από MenuItemRequest
        List<MenuItem> items = request.items().stream()
                .map(req -> toMenuItem(req, menu))
                .toList();

        // 3. Σύνδεση items με menu
        menu.setItems(items);

        // 4. Save (cascade → σώζει και items)
        Menu savedMenu = menuRepository.save(menu);

        return menuMapper.convertMenuToMenuView(savedMenu);
    }




    @Transactional
    @Override
    public void updateMenu(Long menuId, MenuEditForm form) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException());

        menu.setTitle(form.getTitle());

        // υπάρχοντα items (map για εύρεση)
        Map<Long, MenuItem> existing =
                menu.getItems().stream()
                        .collect(Collectors.toMap(MenuItem::getMenuItemId, i -> i));

        for (MenuItemUpdateForm itemForm : form.getItems()) {

            // DELETE
            if (itemForm.delete() && itemForm.id() != null) {
                menu.removeItem(existing.get(itemForm.id()));
                continue;
            }

            // UPDATE
            if (itemForm.id() != null) {
                MenuItem item = existing.get(itemForm.id());
                item.setName(itemForm.name());
                item.setPrice(itemForm.price());
                item.setDescription(itemForm.description());
            }

            // CREATE NEW
            if (itemForm.id() == null && !itemForm.delete()) {
                MenuItem newItem = new MenuItem();
                newItem.setName(itemForm.name());
                newItem.setPrice(itemForm.price());
                newItem.setDescription(itemForm.description());
                menu.addItem(newItem);
            }
        }
    }


    @Override
    public void deactivateMenu(Long menuId) {
        if (menuId == null || menuId < 0) {
            throw new IllegalArgumentException();
        }

        //-----------------------------------------
        final Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found"));

        //-----------------------------------------
        final CurrentUser currentUser = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new SecurityException("Authentication required"));

        if (currentUser.type() != PersonType.RESTAURANT) {
            throw new SecurityException("Restaurant role required");
        }

        if (!menu.getRestaurant().getId().equals(currentUser.id())) {
            throw new SecurityException("Not your menu");
        }

        //-----------------------------------------
        menu.setActive(false);
        menuRepository.save(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuView> getMyMenu() {

        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();

        if (currentUser.type() != PersonType.RESTAURANT) {
            throw new SecurityException("Restaurant role required");
        }

        return menuRepository
                .findByRestaurantIdAndActiveTrue(currentUser.id())
                .map(menuMapper::convertMenuToMenuView);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuView> getMenuByRestaurant(Long restaurantId) {
        if (restaurantId == null) throw new NullPointerException();
        if (restaurantId <= 0) throw new IllegalArgumentException();

        return menuRepository
                .findByRestaurantIdAndActiveTrue(restaurantId)
                .map(menuMapper::convertMenuToMenuView);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuPublicView> getPublicMenuByRestaurant(Long restaurantId) {

        if (restaurantId == null || restaurantId <= 0) {
            throw new IllegalArgumentException("Invalid restaurant id");
        }

        return menuRepository
                .findByRestaurantIdAndActiveTrue(restaurantId)
                .map(menu -> new MenuPublicView(
                        menu.getTitle(),
                        menu.getRestaurant().getUsername(),
                        menu.getItems().stream()
                                .map(item -> new MenuItemPublicView(
                                        item.getMenuItemId(),          // ⬅️ ΑΠΑΡΑΙΤΗΤΟ
                                        item.getName(),
                                        item.getDescription(),
                                        item.getPrice()
                                ))

                                .toList()
                ));
    }




}
