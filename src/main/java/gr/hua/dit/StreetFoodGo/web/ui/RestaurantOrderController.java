package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.OrderService;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
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

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    public RestaurantOrderController(OrderService orderService,
                                     CurrentUserProvider currentUserProvider) {
        this.orderService = orderService;
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
                orderService.getOrders();

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

        OrderView order = orderService.getOrder(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("order", order);
        return "order";
    }

    // -----------------------------
    // ACTIONS
    // -----------------------------
    @PostMapping("/{orderId}/accept")
    public String accept(@PathVariable long orderId) {
        orderService.acceptOrder(orderId);
        return "redirect:/orders/" + orderId;
    }

    @PostMapping("/{orderId}/reject")
    public String reject(@PathVariable long orderId) {
        orderService.rejectOrder(orderId);
        return "redirect:/orders";
    }

    @PostMapping("/{orderId}/start")
    public String start(@PathVariable long orderId) {
        orderService.startOrder(orderId);
        return "redirect:/orders/" + orderId;
    }

    @PostMapping("/{orderId}/complete")
    public String complete(@PathVariable long orderId) {
        orderService.completeOrder(orderId);
        return "redirect:/orders";
    }
}

