package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * UI controller for managing homepage.
 */
@Controller
public class HomepageController {

    private final CurrentUserProvider currentUserProvider;

    public HomepageController(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/")
    public String showHomepage(Model model) {

        // optional current user
        currentUserProvider.getCurrentUser()
                .ifPresent(user -> model.addAttribute("currentUser", user));

        return "homepage";
    }
}


