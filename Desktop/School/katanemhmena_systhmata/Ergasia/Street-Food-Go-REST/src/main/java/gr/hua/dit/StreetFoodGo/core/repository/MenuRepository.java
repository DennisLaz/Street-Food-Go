package gr.hua.dit.StreetFoodGo.core.repository;

import gr.hua.dit.StreetFoodGo.core.model.Menu;
import gr.hua.dit.StreetFoodGo.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findByRestaurantIdAndActiveTrue(Long restaurantId);

    boolean existsByRestaurantIdAndActiveTrue(Long restaurantId);

    Optional<Menu> findActiveMenuByRestaurantId(@Param("restaurantId") Long restaurantId);


}

