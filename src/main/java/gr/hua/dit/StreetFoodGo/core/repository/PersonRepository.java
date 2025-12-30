package gr.hua.dit.StreetFoodGo.core.repository;

import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Person} entity.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByUsername(final String huaId);

    Optional<Person> findByEmailAddressIgnoreCase(final String emailAddress);

    List<Person> findAllByTypeOrderByLastName(final PersonType type);

    boolean existsByEmailAddressIgnoreCase(final String emailAddress);

    boolean existsByPhoneNumber(final String phoneNumber);

    boolean existsByUsernameIgnoreCase(final String huaId);
}
