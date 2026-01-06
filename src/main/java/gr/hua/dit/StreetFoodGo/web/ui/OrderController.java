package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUser;
import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.OrderService;
import gr.hua.dit.StreetFoodGo.core.service.mapper.OrderItemMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.*;
import gr.hua.dit.StreetFoodGo.web.ui.model.CompleteOrderForm;
import gr.hua.dit.StreetFoodGo.web.ui.model.OpenOrderForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UI controller for managing orders
 */
@Controller
public class OrderController {

    private final CurrentUserProvider currentUserProvider;
    private final OrderService orderService;
    private final OrderItemMapper orderItemMapper;

    public OrderController(
            CurrentUserProvider currentUserProvider,
            OrderService orderService,
            OrderItemMapper orderItemMapper
    ) {
        this.currentUserProvider = currentUserProvider;
        this.orderService = orderService;
        this.orderItemMapper = orderItemMapper;
    }


    @GetMapping("")
    public String list(final Model model){
        final List<OrderView> orderViewList=this.orderService.getOrders();
        model.addAttribute("orders",orderViewList);
        return "orders";
    }



    // 🔐 CUSTOMER ONLY
    @PostMapping("/orders/add-item")
    public String addItemToOrder(@RequestParam Long menuItemId) {

        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        orderService.addItemToOpenOrder(user.id(), menuItemId);

        return "redirect:/my_order";
    }

    @PostMapping("/my_order/remove-item")
    public String removeItemFromOrder(@RequestParam Long menuItemId) {
        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        orderService.removeItemFromOpenOrder(user.id(), menuItemId);

        return "redirect:/my_order";
    }


    @GetMapping("/my_order")
    public String myOrder(Model model) {

        CurrentUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.type() != PersonType.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        OrderView order = orderService
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

        orderService.submitOrder(user.id());

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

        OrderView order = orderService
                .getOpenOrderForCurrentCustomer(user.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("order",order);
        return "order_pending";
    }


}
