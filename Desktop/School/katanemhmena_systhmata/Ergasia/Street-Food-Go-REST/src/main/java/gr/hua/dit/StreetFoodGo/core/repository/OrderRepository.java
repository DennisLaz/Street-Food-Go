package gr.hua.dit.StreetFoodGo.core.repository;

import gr.hua.dit.StreetFoodGo.core.model.Order;
import gr.hua.dit.StreetFoodGo.core.model.OrderStatus;
import gr.hua.dit.StreetFoodGo.core.service.model.OrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllByRestaurantId(final Long restaurantId);

    List<Order> findAllByCustomerId(final Long customerId);

    Optional<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);

    boolean existsByCustomerIdAndRestaurantIdAndStatusIn(final Long customerId, final Long restaurantId, Collection<OrderStatus> statuses);

    Optional<Order> findOpenByCustomerId(Long customerId);

}
