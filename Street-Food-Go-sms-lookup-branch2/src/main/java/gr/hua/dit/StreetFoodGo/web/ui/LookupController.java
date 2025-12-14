package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.port.impl.dto.LookupResult;
import gr.hua.dit.StreetFoodGo.core.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lookups")
public class LookupController {

    private final PersonService personService;

    public LookupController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<LookupResult> lookup(@PathVariable String username) {

        Person person = personService.findByUsername(username);

        if (person == null) {
            // user NOT found
            LookupResult result = new LookupResult(
                    username,   // raw
                    false,      // exists
                    null,       // huaId
                    null        // type
            );
            return ResponseEntity.ok(result);
        }

        // user found
        LookupResult result = new LookupResult(
                username,           // raw
                true,               // exists
                person.getUsername(), // huaId
                person.getType()    // PersonType
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("lookup ok");
    }
}
