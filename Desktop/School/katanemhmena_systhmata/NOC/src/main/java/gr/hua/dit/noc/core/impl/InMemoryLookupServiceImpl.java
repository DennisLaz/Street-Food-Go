package gr.hua.dit.noc.core.impl;

import gr.hua.dit.noc.core.LookupService;
import gr.hua.dit.noc.core.model.LookupResult;
import gr.hua.dit.noc.core.model.PersonType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory implementation of {@link LookupService}.
 *
 * @author Dimitris Gkoulis
 */
@Service
public class InMemoryLookupServiceImpl implements LookupService {

    private final Map<String, PersonType> inMemoryDatabase;

    public InMemoryLookupServiceImpl() {
        this.inMemoryDatabase = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void populateInitialData() {
        this.inMemoryDatabase.put("it2023001", PersonType.RESTAURANT);
        this.inMemoryDatabase.put("it2023002", PersonType.RESTAURANT);
        this.inMemoryDatabase.put("t0001", PersonType.CUSTOMER);
        this.inMemoryDatabase.put("t0002", PersonType.CUSTOMER);
        this.inMemoryDatabase.put("s0001", PersonType.STAFF);
        this.inMemoryDatabase.put("s0002", PersonType.STAFF);
    }

    @Override
    public LookupResult lookupByUsername(final String username) {
        if (username == null) throw new NullPointerException("username cannot be null");
        if (username.isBlank()) throw new IllegalArgumentException("username cannot be blank");
        final String normalizedHuaId = username.strip().toLowerCase();
        final PersonType type = this.inMemoryDatabase.get(normalizedHuaId);
        if (type == null) {
            return LookupResult.empty(username);
        } else {
            return new LookupResult(username, true, normalizedHuaId, type);
        }
    }
}
