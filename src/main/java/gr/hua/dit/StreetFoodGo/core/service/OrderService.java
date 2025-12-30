package gr.hua.dit.StreetFoodGo.core.service;

import gr.hua.dit.StreetFoodGo.core.service.model.CompleteOrderRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OpenOrderRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
import gr.hua.dit.StreetFoodGo.core.service.model.StartOrderRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link Order}
 *
 * <p>
 *     <strong>All methods MUST be {@link gr.hua.dit.StreetFoodGo.core.security.CurrentUser}</strong>
 * </p>
 */
public interface OrderService {
    Optional<OrderView> getOrder(Long id);

    List<OrderView> getOrders();

    OrderView openOrder (final OpenOrderRequest openOrderRequest, final boolean notify);

    default OrderView openOrder(final OpenOrderRequest openOrderRequest){
        return this.openOrder(openOrderRequest, true);
    }

    OrderView startOrder (final StartOrderRequest startOrderRequest);

    OrderView completeOrder(final CompleteOrderRequest completeOrderRequest);
}
