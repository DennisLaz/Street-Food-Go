package gr.hua.dit.StreetFoodGo.web.ui;

import org.springframework.security.core.Authentication;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonResult;
import gr.hua.dit.StreetFoodGo.core.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * UI controller for managing teacher/student registration.
 */
@Controller
public class RegistrationController {

    private final PersonService personService;

    public RegistrationController(final PersonService personService) {
        if (personService == null) throw new NullPointerException();
        this.personService = personService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(final Authentication authentication, Model model) {
        if (AuthController.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }
        // TODO if user is authenticated, redirect to tickets
        // Initial data for the form.
        final CreatePersonRequest createPersonRequest =
                new CreatePersonRequest(
                        PersonType.CUSTOMER,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                );        model.addAttribute("createPersonRequest", createPersonRequest);
        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(
            final Authentication authentication,
            @ModelAttribute("createPersonRequest") final CreatePersonRequest createPersonRequest,
            final Model model
    ) {
        if (AuthController.isAuthenticated(authentication)) {
            return "redirect:/profile"; // already logged in
        }
        // TODO Form validation + UI errors

        final CreatePersonResult createPersonResult = this.personService.createPerson(createPersonRequest);
        if (createPersonResult.created()) {
            return "redirect:/login"; // registration successful
        }
        model.addAttribute("createPersonResult", createPersonResult); // Pass the same form data.
        model.addAttribute("errorMessage", createPersonResult.reason()); // Show an error message!
        return "register";
    }
}