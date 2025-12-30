package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.security.CurrentUserProvider;
import gr.hua.dit.StreetFoodGo.core.service.OrderService;
import gr.hua.dit.StreetFoodGo.core.service.model.CompleteOrderRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OpenOrderRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
import gr.hua.dit.StreetFoodGo.core.service.model.StartOrderRequest;
import gr.hua.dit.StreetFoodGo.web.ui.model.CompleteOrderForm;
import gr.hua.dit.StreetFoodGo.web.ui.model.OpenOrderForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * UI controller for managing orders
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final CurrentUserProvider  currentUserProvider;
    private final OrderService  orderService;

    public OrderController(CurrentUserProvider currentUserProvider, OrderService orderService) {
        if(currentUserProvider==null)throw new NullPointerException();
        if(orderService==null)throw new NullPointerException();
        this.currentUserProvider = currentUserProvider;
        this.orderService = orderService;
    }

    @GetMapping("")
    public String list(final Model model){
        final List<OrderView> orderViewList=this.orderService.getOrders();
        model.addAttribute("orders",orderViewList);
        return "orders";
    }

    @GetMapping("/{orderId}")
    public String detail(@PathVariable long  orderId, final Model model){
        final OrderView orderView = this.orderService.getOrder(orderId).orElse(null);
        if(orderView==null){
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Order not found");
        }
        model.addAttribute("order",orderView);
        return "order";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/new")
    public String showOpenForm(final Model model){
        final OpenOrderForm openOrderForm=new OpenOrderForm(null,"","");
        model.addAttribute("form", openOrderForm);
        return "new_order";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/new")
    public String handleOpenForm(@ModelAttribute("form") @Valid final OpenOrderForm openOrderForm,
                                 final BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return "new_order";
        }
        final OpenOrderRequest openOrderRequest=new OpenOrderRequest(
                this.currentUserProvider.requiredCustomerId() ,
                openOrderForm.restaurantId(),
                openOrderForm.subject(),
                openOrderForm.customerContent()
        );
        final OrderView orderView = this.orderService.openOrder(openOrderRequest);

        return "redirect:/orders/"+orderView.orderId();
    }

    @PreAuthorize("hasRole('RESTAURANT')")
    @PostMapping("{orderId}/start")
    public String handleStartForm(@PathVariable long orderId){
        final StartOrderRequest startOrderRequest=new StartOrderRequest(orderId);
        final OrderView orderView = this.orderService.startOrder(startOrderRequest);
        return "redirect:/orders/"+orderView.orderId();
    }

    @PreAuthorize("hasRole('RESTAURANT')")
    @PostMapping("{orderId}/complete")
    public String handleCompleteForm(@PathVariable long orderId,
                                     @ModelAttribute("form") final CompleteOrderForm completeOrderForm,
                                     final BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "order";
        }
        final CompleteOrderRequest completeOrderRequest=new CompleteOrderRequest(
                orderId,
                completeOrderForm.restaurantContent()
        );
        final OrderView orderView = this.orderService.completeOrder(completeOrderRequest);
        return "redirect:/orders/"+orderView.orderId();
    }


}
