package gr.hua.dit.StreetFoodGo.core.service;

import gr.hua.dit.StreetFoodGo.core.model.Client;
import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.repository.ClientRepository;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonRequest;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuCreateRequest;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuItemRequest;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Initializes application
 */
@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final ClientRepository clientRepository;
    private final PersonRepository personRepository;
    private final OrderBusinessLogicService orderBusinessLogicService;
    private final PersonBusinessLogicService personBusinessLogicService;
    private final AtomicBoolean initialized;
    private final MenuService menuService;

    public InitializationService(final ClientRepository clientRepository,
                                 final PersonRepository personRepository,
                                 final PersonBusinessLogicService personBusinessLogicService,
                                 final OrderBusinessLogicService orderBusinessLogicService,
                                 final MenuService menuService) {
        if (clientRepository == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (personBusinessLogicService == null) throw new NullPointerException();
        if (orderBusinessLogicService == null) throw new NullPointerException();
        if (menuService == null) throw new NullPointerException();

        this.clientRepository = clientRepository;
        this.personRepository = personRepository;
        this.orderBusinessLogicService = orderBusinessLogicService;
        this.personBusinessLogicService = personBusinessLogicService;
        this.initialized = new AtomicBoolean(false);
        this.menuService = menuService;
    }

    @PostConstruct
    public void populateDatabaseWithInitialData(){
        final boolean alreadyInitialized = this.initialized.getAndSet(true);
        if (alreadyInitialized){
            LOGGER.warn("Database already initialized");
            return;
        }
        LOGGER.info("Starting database initialization with initial data");

        final List<Client> clientList = List.of(
                new Client(null, "client01", "s3cr3t", "INTEGRATION_READ,INTEGRATION_WRITE"),
                new Client(null, "client02", "s3cr3t", "INTEGRATION_WRITE")
        );

        //Δημιουργια αρχικων Profiles
        this.clientRepository.saveAll(clientList);
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
                    "6900000004",
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
                ),

                new CreatePersonRequest(
                        PersonType.RESTAURANT,
                        "StreetTacos",
                        "Street",
                        "Tacos",
                        "streettacose@gmail.com",
                        "pindou 64",
                        "6900000002",
                        "12345"
                ),

                new CreatePersonRequest(
                        PersonType.RESTAURANT,
                        "BurgerHouse",
                        "Burger",
                        "House",
                        "burgerhouse@gmail.com",
                        "pindou 65",
                        "6900000003",
                        "12345"
                )


        );
        for (final var createPersonRequest : createPersonRequestList){
            this.personBusinessLogicService.createPerson(createPersonRequest, false); // do not send SMS
        }

        LOGGER.info("Database initialization completed successfully");

        //Δημιουργια 2 Αρχικων Menu
        MenuCreateRequest request = new MenuCreateRequest(
            "Burger House Menu",
            List.of(
                    new MenuItemRequest(
                            "Classic Burger",
                            new BigDecimal("7.50"),
                            "Beef patty, cheddar, lettuce, tomato"
                    ),
                    new MenuItemRequest(
                            "Cheese Bacon Burger",
                            new BigDecimal("8.90"),
                            "Beef patty, bacon, cheddar, BBQ sauce"
                    ),
                    new MenuItemRequest(
                            "French Fries",
                            new BigDecimal("3.00"),
                            "Crispy fries with sea salt"
                    )
                )
            );
        Person burgerHouse = personRepository.findByUsername("BurgerHouse").orElseThrow();
        menuService.createMenuForRestaurant(burgerHouse, request);



        MenuCreateRequest request1 =  new MenuCreateRequest(
                "Street Tacos",
                List.of(
                        new MenuItemRequest(
                                "Chicken Taco",
                                new BigDecimal("3.20"),
                                "Grilled chicken, onion, coriander"
                        ),
                        new MenuItemRequest(
                                "Pork Taco",
                                new BigDecimal("3.50"),
                                "Slow cooked pork, salsa roja"
                        ),
                        new MenuItemRequest(
                                "Guacamole",
                                new BigDecimal("2.80"),
                                "Fresh avocado, lime, salt"
                        )
                    )
                );
        Person streetTacos = personRepository.findByUsername("StreetTacos").orElseThrow();
        menuService.createMenuForRestaurant(streetTacos, request1);
    }
}
