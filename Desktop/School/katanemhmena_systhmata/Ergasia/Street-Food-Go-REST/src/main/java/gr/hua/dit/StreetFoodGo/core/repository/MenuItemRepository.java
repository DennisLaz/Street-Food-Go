package gr.hua.dit.StreetFoodGo.core.repository;

import gr.hua.dit.StreetFoodGo.core.model.Client;
import gr.hua.dit.StreetFoodGo.core.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Repository for {@link MenuItem} entiry.
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
