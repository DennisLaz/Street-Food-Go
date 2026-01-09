package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.OrderBusinessLogicService;
import gr.hua.dit.StreetFoodGo.core.service.mapper.OrderItemMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * UI controller for managing orders
 */
@Controller
public class OrderController {

    private final CurrentUserProvider currentUserProvider;
    private final OrderBusinessLogicService orderBusinessLogicService;
    private final OrderItemMapper orderItemMapper;

    public OrderController(
            CurrentUserProvider currentUserProvider,
            OrderBusinessLogicService orderBusinessLogicService,
            OrderItemMapper orderItemMapper
    ) {
        this.currentUserProvider = currentUserProvider;
        this.orderBusinessLogicService = orderBusinessLogicService;
        this.orderItemMapper = orderItemMapper;
    }

    // 🔐 CUSTOMER ONLY
    @PostMapping("/orders/add-item")
    public String addItemToOrder(@RequestParam Long menuItemId) {

        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        orderBusinessLogicService.addItemToOpenOrder(user.id(), menuItemId);

        return "redirect:/my_order";
    }

    @PostMapping("/my_order/remove-item")
    public String removeItemFromOrder(@RequestParam Long menuItemId) {
        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        orderBusinessLogicService.removeItemFromOpenOrder(user.id(), menuItemId);

        return "redirect:/my_order";
    }


    @GetMapping("/my_order")
    public String myOrder(Model model) {

        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        OrderView order = orderBusinessLogicService
                .getOpenOrderForCurrentCustomer(user.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("order", order);
        return "my_order";
    }

    @PostMapping("/my_order/submit")
    public String submitOrder() {

        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        orderBusinessLogicService.submitOrder(user.id());

        return "redirect:/order_pending";
    }


    // -----------------------------
    // STEP 3: Waiting page
    // -----------------------------
    @GetMapping("/order_pending")
    public String orderPending(Model model) {
        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        OrderView order = orderBusinessLogicService
                .getOpenOrderForCurrentCustomer(user.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("order",order);
        return "order_pending";
    }


}
