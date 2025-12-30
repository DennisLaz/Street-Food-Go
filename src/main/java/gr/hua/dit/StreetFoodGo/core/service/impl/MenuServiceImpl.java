package gr.hua.dit.StreetFoodGo.core.service.impl;

import gr.hua.dit.StreetFoodGo.core.model.Menu;
import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.repository.MenuRepository;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.MenuService;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuCreateRequest;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuItemPublicView;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuPublicView;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuUpdateRequest;
import gr.hua.dit.StreetFoodGo.core.service.mapper.MenuMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.MenuView;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menu.setTitle(createMenuDto.title());
        menu.setActive(true);

        final Menu savedMenu = menuRepository.save(menu);

        return menuMapper.convertMenuToMenuView(savedMenu);
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

        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menu.setTitle(request.title());
        menu.setActive(true);

        return menuMapper.convertMenuToMenuView(menuRepository.save(menu));
    }



    @Override
    public MenuView updateMenu(Long menuId, @Valid MenuUpdateRequest updateMenuDto) {
        if (menuId == null || updateMenuDto == null) {
            throw new NullPointerException();
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
        if (updateMenuDto.title() != null) {
            menu.setTitle(updateMenuDto.title());
        }

        if (!updateMenuDto.active() ) {
            menu.setActive(updateMenuDto.active());
        }

        final Menu savedMenu = menuRepository.save(menu);

        return menuMapper.convertMenuToMenuView(savedMenu);
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
                                        item.getName(),
                                        item.getPrice(),
                                        item.getDescription()
                                ))
                                .toList()
                ));
    }




}
