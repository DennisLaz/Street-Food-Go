package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.OrderBusinessLogicService;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
import gr.hua.dit.StreetFoodGo.core.service.model.StartOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class RestaurantOrderController {

    private final OrderBusinessLogicService orderBusinessLogicService;
    private final CurrentUserProvider currentUserProvider;

    public RestaurantOrderController(OrderBusinessLogicService orderBusinessLogicService,
                                     CurrentUserProvider currentUserProvider) {
        this.orderBusinessLogicService = orderBusinessLogicService;
        this.currentUserProvider = currentUserProvider;
    }

    // -----------------------------
    // LIST ORDERS
    // -----------------------------
    @GetMapping
    public String list(Model model) {

        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.RESTAURANT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<OrderView> orders =
                orderBusinessLogicService.getOrders();

        model.addAttribute("orders", orders);
        return "orders";
    }

    // -----------------------------
    // ORDER DETAILS
    // -----------------------------
    @GetMapping("/{orderId}")
    public String detail(@PathVariable long orderId, Model model) {

        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.RESTAURANT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        OrderView order = orderBusinessLogicService.getOrder(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("order", order);
        return "order";
    }

    // -----------------------------
    // ACTIONS
    // -----------------------------
    @PostMapping("/{orderId}/accept")
    public String accept(@PathVariable long orderId) {
        orderBusinessLogicService.acceptOrder(orderId);
        return "redirect:/orders/" + orderId;
    }

    @PostMapping("/{orderId}/reject")
    public String reject(@PathVariable long orderId) {
        orderBusinessLogicService.rejectOrder(orderId);
        return "redirect:/orders";
    }

    @PostMapping("/{orderId}/start")
    public String start(@PathVariable long orderId) {
        final StartOrderRequest request =
                new StartOrderRequest(orderId);

        orderBusinessLogicService.startOrder(request);
        return "redirect:/orders/" + orderId;
    }

    @PostMapping("/{orderId}/complete")
    public String complete(@PathVariable long orderId) {
        orderBusinessLogicService.completeOrder(orderId);
        return "redirect:/orders";
    }
}

