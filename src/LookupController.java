package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.port.LookupPort;
import gr.hua.dit.StreetFoodGo.core.port.impl.dto.LookupResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lookups")
public class LookupController {

    private final LookupPort lookupPort;

    public LookupController(final LookupPort lookupPort) {
        this.lookupPort = lookupPort;
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<LookupResult> lookup(@PathVariable String username) {

        Optional<PersonType> result = lookupPort.lookup(username);

        LookupResult dto;

        if (result.isPresent()) {
            // Found user → create populated LookupResult
            dto = new LookupResult(
                    username,      // raw
                    true,          // exists
                    username,      // huaId → ή βάλε κάτι άλλο αν έχεις άλλο ID
                    result.get()   // type
            );
        } else {
            // Not found → exists=false, rest = null
            dto = new LookupResult(
                    username,  // raw
                    false,     // exists
                    null,      // huaId
                    null       // type
            );
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "lookup controller OK"));
    }
}
