package gr.hua.dit.StreetFoodGo.core.service;


import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;

import java.util.List;

/**
 * Service for managing {@code Order} for data analytics purposes
 */
public interface OrderDataService {

    List<OrderView> getAllOrders();
}
