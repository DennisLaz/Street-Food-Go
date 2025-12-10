package gr.hua.dit.StreetFoodGo.core.service.impl;

import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.port.SmsNotificationPort;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.service.PersonService;
import gr.hua.dit.StreetFoodGo.core.service.mapper.PersonMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonResult;
import gr.hua.dit.StreetFoodGo.core.service.model.PersonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link gr.hua.dit.StreetFoodGo.core.service.PersonService}
 */
@Service
public class PersonServiceImpl implements PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final SmsNotificationPort smsNotificationPort;


    public PersonServiceImpl(final PasswordEncoder passwordEncoder,
                             PersonRepository personRepository,
                             final PersonMapper personMapper,
                             final SmsNotificationPort smsNotificationPort) {
        if (passwordEncoder == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (personMapper == null) throw new NullPointerException();
        if (smsNotificationPort == null) throw new NullPointerException();


        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.smsNotificationPort = smsNotificationPort;
    }


    @Override
    public CreatePersonResult createPerson (final CreatePersonRequest createPersonRequest, final boolean notify) {
        if (createPersonRequest == null) throw new NullPointerException();

        // Unpack (we assume validated CreatePersonRequest)
        // ------------------------------

        final PersonType type = createPersonRequest.type();
        final String username = createPersonRequest.username().strip();
        final String firstName = createPersonRequest.firstName().strip();
        final String lastName = createPersonRequest.lastName().strip();
        final String emailAddress = createPersonRequest.emailAddress().strip();
        final String address = createPersonRequest.address().strip();
        final String phoneNumber = createPersonRequest.phoneNumber().strip();
        final String rawPassword = createPersonRequest.rawPassword();

        // Basic email address validation
        // -------------------------------

        // TODO if needed down the line

        // -------------------------------

        // TODO username uniqueness
        // TODO email uniqueness
        // TODO mobilePhoneNumber uniqueness

        // -------------------------------

        // TODO  sms call

        // -------------------------------

        final String hashedPassword = this.passwordEncoder.encode(rawPassword);
        // -------------------------------

        // Instantiate person.
        // --------------------------------------------------

        Person person = new Person();
        person.setId(null); // auto generated
        person.setUsername(username);
        person.setType(type);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmailAddress(emailAddress);
        person.setAddress(address);
        person.setPhoneNumber(phoneNumber);
        person.setPasswordHash(hashedPassword);
        person.setCreatedAt(null); // auto generated.

        // Persist person (save/insert to database)
        // --------------------------------------------------

        person = this.personRepository.save(person);

        // --------------------------------------------------

        if (notify) {
            final String content = String.format(
                    "You have successfully registered for Street Food Go" +
                            "Use your email (%s) to log in.", emailAddress);
            final boolean sent = this.smsNotificationPort.sendSms(phoneNumber, content);
            if (!sent) {
                LOGGER.warn("");
            }
        }

        // Map `Person` to `PersonView`.
        // --------------------------------------------------

        final PersonView personView = this.personMapper.convertPersonToPersonView(person);

        // -------------------------------------------------

        return CreatePersonResult.success(personView);
    }
}
