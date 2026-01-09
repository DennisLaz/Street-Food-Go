package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.MenuService;
import gr.hua.dit.StreetFoodGo.core.service.OrderBusinessLogicService;
import gr.hua.dit.StreetFoodGo.core.service.mapper.MenuMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.MenuView;
import gr.hua.dit.StreetFoodGo.web.ui.model.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class MenuController {

    private final PersonRepository personRepository;
    private final MenuService menuService;
    private final CurrentUserProvider currentUserProvider;
    private final MenuMapper menuMapper;
    private final OrderBusinessLogicService orderBusinessLogicService;

    public MenuController(MenuService menuService ,  PersonRepository personRepository,  CurrentUserProvider currentUserProvider , MenuMapper menuMapper, OrderBusinessLogicService orderBusinessLogicService) {
        if (menuService == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        if (menuMapper == null) throw new NullPointerException();
        if (orderBusinessLogicService == null) throw new NullPointerException();
        this.menuService = menuService;
        this.personRepository = personRepository;
        this.currentUserProvider = currentUserProvider;
        this.menuMapper = menuMapper;
        this.orderBusinessLogicService = orderBusinessLogicService;
    }

    private List<MenuItemRequest> mapItems(List<MenuItemForm> items) {
        return items.stream()
                .map(item -> new MenuItemRequest(
                        item.getName(),
                        item.getPrice(),
                        item.getDescription()
                ))
                .toList();
    }


    // 🔐 RESTAURANT: Create menu (FORM SUBMIT)
    @GetMapping("/menu_create")
    public String showCreateMenuForm(Model model) {

        // 1️⃣ Είναι logged in;
        Optional<CurrentUser> currentUserOpt = currentUserProvider.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        CurrentUser currentUser = currentUserOpt.get();

        // 2️⃣ Είναι RESTAURANT;
        if (currentUser.type() != PersonType.RESTAURANT) {
            return "redirect:/";
        }

        // 3️⃣ Έχει ήδη menu;
        if (menuService.getMyMenu().isPresent()) {
            return "redirect:/menu_edit";
        }

        // 4️⃣ OK → show form
        model.addAttribute("form", new MenuCreateForm());
        return "menu_create";
    }


    @PostMapping("/menu_create")
    public String createMenu(

            @Valid @ModelAttribute("form") MenuCreateForm form
    ) {

        menuService.createMenu(
                new MenuCreateRequest(
                        form.getTitle(),
                        mapItems(form.getItems())
                )
        );
        return "redirect:/my_menu";
    }


    @GetMapping("/menu_edit")
    public String editMenu(Model model, RedirectAttributes redirectAttributes) {

        Optional<MenuView> menuOpt = menuService.getMyMenu();

        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Menu does not exist yet. Create one!"
            );
            return "redirect:/menu_create";
        }

        MenuView menu = menuOpt.get();



        MenuEditForm form = menuMapper.toEditForm(menu);
        model.addAttribute("form", form);
        model.addAttribute("menuId", menu.menuId());

        return "menu_edit";
    }


    @PostMapping("/menu_edit/{menuId}")
    public String updateMenu(
            @PathVariable Long menuId,
            @Valid @ModelAttribute("form") MenuEditForm form
    ) {
        menuService.updateMenu(menuId, form);
        return "redirect:/my_menu";
    }



    // 🔐 RESTAURANT: Get own menu
    @GetMapping("/my_menu")
    public String myMenu(Model model, RedirectAttributes redirectAttributes) {
        Optional<MenuView> menuOpt = menuService.getMyMenu();

        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Menu does not exist yet. Create one!"
            );
            return "redirect:/menu_create";
        }

        model.addAttribute("menu",
                menuService.getMyMenu().orElse(null)
        );
        return "my_menu";
    }

    //Find all restaurants
    @GetMapping("/restaurants")
    public String restaurants(Model model) {
        model.addAttribute("restaurants", personRepository.findAllByTypeOrderByLastName(PersonType.RESTAURANT));
        return "restaurants";
    }

    // 🌍 PUBLIC: Get active menu by restaurant
    @GetMapping("/restaurants/{restaurantId}")
    public String viewRestaurantMenu(
            @PathVariable Long restaurantId,
            Model model
    ) {
        model.addAttribute(
                "menu",
                menuService.getPublicMenuByRestaurant(restaurantId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        );
        model.addAttribute("restaurantId", restaurantId);
        return "show_menus";
    }

    @PostMapping("/restaurants/{restaurantId}/add-item")
    public String addItemFromMenu(
            @PathVariable Long restaurantId,
            @RequestParam Long menuItemId
    ) {
        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        orderBusinessLogicService.addItemToOpenOrder(
                user.id(),
                restaurantId,
                menuItemId
        );

        return "redirect:/restaurants/" + restaurantId;
    }




}
