package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.service.MenuService;
import gr.hua.dit.StreetFoodGo.core.service.PersonService;
import gr.hua.dit.StreetFoodGo.core.service.model.MenuView;
import gr.hua.dit.StreetFoodGo.web.ui.model.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
public class MenuController {

    private final PersonRepository personRepository;
    private final MenuService menuService;

    public MenuController(MenuService menuService ,  PersonRepository personRepository) {
        if (menuService == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        this.menuService = menuService;
        this.personRepository = personRepository;
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



    // 🔐 RESTAURANT: Update menu (FORM SUBMIT)
    @PostMapping("/menu_edit/{menu_id}")
    public String updateMenu(
            @PathVariable Long menu_id,
            @Valid @ModelAttribute MenuUpdateRequest request
    ) {
        menuService.updateMenu(menu_id, request);
        return "redirect:/my_menu";
    }


    // 🔐 RESTAURANT: Get own menu
    @GetMapping("/my_menu")
    public String myMenu(Model model) {
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
        return "show_menus";
    }



}
