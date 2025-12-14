package gr.hua.dit.StreetFoodGo.core.service;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonRequest;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Initializes application
 */
@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final PersonService personService;
    private final AtomicBoolean intialized;

    public InitializationService(PersonService personService){
        if (personService == null) throw new NullPointerException();
        this.personService = personService;
        this.intialized = new AtomicBoolean(false);
    }

    @PostConstruct
    public void populateDatabaseWithInitialData(){
        final boolean alreadyInitialized = this.intialized.getAndSet(true);
        if (alreadyInitialized){
            LOGGER.warn("Database already initialized");
            return;
        }
        LOGGER.info("Starting database initialization with initial data");
        final List<CreatePersonRequest> createPersonRequestList = List.of(
                new CreatePersonRequest(
                        PersonType.CUSTOMER,
                        "george",
                        "George",
                        "Ioannidis",
                        "george@gmail.com",
                        "eirinis 10",
                        "6900000000",
                        "12345"
                ),
                new CreatePersonRequest(
                    PersonType.RESTAURANT,
                    "McDonalds",
                    "Bob",
                    "Brown",
                    "mcdonalds@hotmail.com",
                    "kifisias 100",
                    "6900000002",
                    "12345"
                ),
                new CreatePersonRequest(
                        PersonType.RESTAURANT,
                        "slato",
                        "Slato",
                        "Mortale",
                        "saltomortale@gmail.com",
                        "pindou 62",
                        "6900000001",
                        "12345"
                )


        );
        for (final var createPersonRequest : createPersonRequestList){
            this.personService.createPerson(createPersonRequest, false); // do not send SMS
        }
        LOGGER.info("Database initialization completed successfully");
    }
}
