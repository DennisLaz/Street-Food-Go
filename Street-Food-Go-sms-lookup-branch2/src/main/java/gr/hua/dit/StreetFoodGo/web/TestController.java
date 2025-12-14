package gr.hua.dit.StreetFoodGo.web;

import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for testing.
 */
@RestController
public class TestController {

    private final PersonRepository personRepository;

    public TestController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test() {
        for (int i=0; i<100; i++) {
            Person person1 = new Person();
            person1.setId(null);
            person1.setUsername("bob" + i);
            person1.setFirstName("Bobby " + i);
            person1.setLastName("Brown" + i);
            person1.setEmailAddress("bob@gmail.com");
            person1.setPhoneNumber("+306900000000");
            person1.setType(PersonType.CUSTOMER);
            person1.setAddress("Some street " + i);
            person1.setPasswordHash("<hash>");

            person1 = this.personRepository.save(person1);
        }

        final var people = this.personRepository
                .findAllByTypeOrderByLastName(PersonType.CUSTOMER);

        final String stringToServe = String.join(
                "\n", people.stream().map(Person::toString).toList());

        return stringToServe;
    }
}
