package gr.hua.dit.StreetFoodGo.core.service;

import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.OpenOrderRequest;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuCreateRequest;
import gr.hua.dit.StreetFoodGo.web.ui.model.MenuItemRequest;
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

    private final PersonRepository personRepository;
    private final OrderService orderService;
    private final PersonService personService;
    private final AtomicBoolean intialized;
    private final MenuService menuService;

    public InitializationService(final PersonRepository personRepository, final PersonService personService, final OrderService orderService, final MenuService menuService) {
        if (personRepository == null) throw new NullPointerException();
        if (personService == null) throw new NullPointerException();
        if (orderService == null) throw new NullPointerException();
        if (menuService == null) throw new NullPointerException();

        this.personRepository = personRepository;
        this.orderService = orderService;
        this.personService = personService;
        this.intialized = new AtomicBoolean(false);
        this.menuService = menuService;
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
            this.personService.createPerson(createPersonRequest, false); // do not send SMS
        }
        //TODO Not working: requires authenticated user
        /*
        final List<OpenOrderRequest> openOrderRequestList = List.of(
                new OpenOrderRequest(
                        1L,
                        2L,
                        "Test subject",
                        "Test content"
                )
        );
        for (final var openOrderRequest : openOrderRequestList){
            this.orderService.openOrder(openOrderRequest, false); // do not send SMS
        }

        */
                LOGGER.info("Database initialization completed successfully");


            MenuCreateRequest request = new MenuCreateRequest(
                "Burger House Menu",
                List.of(
                        new MenuItemRequest(
                                "Classic Burger",
                                7.50,
                                "Beef patty, cheddar, lettuce, tomato"
                        ),
                        new MenuItemRequest(
                                "Cheese Bacon Burger",
                                8.90,
                                "Beef patty, bacon, cheddar, BBQ sauce"
                        ),
                        new MenuItemRequest(
                                "French Fries",
                                3.00,
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
                                3.20,
                                "Grilled chicken, onion, coriander"
                        ),
                        new MenuItemRequest(
                                "Pork Taco",
                                3.50,
                                "Slow cooked pork, salsa roja"
                        ),
                        new MenuItemRequest(
                                "Guacamole",
                                2.80,
                                "Fresh avocado, lime, salt"
                        )
                    )
                );
        Person streetTacos = personRepository.findByUsername("StreetTacos").orElseThrow();
        menuService.createMenuForRestaurant(streetTacos, request1);






    }
}
