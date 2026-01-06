package gr.hua.dit.StreetFoodGo.core.service;

import gr.hua.dit.StreetFoodGo.core.service.model.CompleteOrderRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OpenOrderRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
import gr.hua.dit.StreetFoodGo.core.service.model.StartOrderRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link gr.hua.dit.StreetFoodGo.core.model.Order}
 *
 * <p>
 *     <strong>All methods MUST be {@link gr.hua.dit.StreetFoodGo.core.security.CurrentUser}</strong>
 * </p>
 */
public interface OrderService {
    Optional<OrderView> getOrder(Long id);

    public void addItemToOpenOrder(Long customerId, Long menuItemId);

    List<OrderView> getOrders();

    OrderView openOrder(final OpenOrderRequest openOrderRequest, final boolean notify);

    default OrderView openOrder(final OpenOrderRequest openOrderRequest) {
        return null;
    }

    OrderView startOrder(final long orderId);

    OrderView startOrder(final StartOrderRequest startOrderRequest);

    OrderView completeOrder(final long orderId);

    OrderView completeOrder(final CompleteOrderRequest completeOrderRequest);

    OrderView acceptOrder(final long orderId);

    void rejectOrder(final long orderId);

    void addItemToOpenOrder(Long customerId, Long restaurantId, Long menuItemId);

    Optional<OrderView> getOpenOrderForCurrentCustomer(long id);

    @Transactional
    void removeItemFromOpenOrder(Long customerId, Long menuItemId);

    void submitOrder(long id);
}
