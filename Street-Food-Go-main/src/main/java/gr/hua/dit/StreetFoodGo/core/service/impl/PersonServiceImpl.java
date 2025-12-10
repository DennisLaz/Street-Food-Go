package gr.hua.dit.StreetFoodGo.core.service.impl;

import gr.hua.dit.StreetFoodGo.core.model.Person;
import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import gr.hua.dit.StreetFoodGo.core.port.LookupPort;
import gr.hua.dit.StreetFoodGo.core.port.SmsNotificationPort;
import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import gr.hua.dit.StreetFoodGo.core.service.mapper.PersonMapper;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonResult;
import gr.hua.dit.StreetFoodGo.core.service.model.PersonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link gr.hua.dit.StreetFoodGo.core.service.PersonService}
 */
@Service
public class PersonServiceImpl implements gr.hua.dit.StreetFoodGo.core.service.PersonService {

    private final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);
    private final LookupPort lookupPort;
    private final SmsNotificationPort smsNotificationPort;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonServiceImpl(final LookupPort lookupPort,
                             final SmsNotificationPort smsNotificationPort,
                             final PasswordEncoder passwordEncoder,
                             final PersonRepository personRepository,
                             final PersonMapper personMapper) {
        if(lookupPort == null) throw new NullPointerException();
        if(smsNotificationPort == null) throw new NullPointerException();
        if (passwordEncoder == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (personMapper == null) throw new NullPointerException();

        this.lookupPort = lookupPort;
        this.smsNotificationPort = smsNotificationPort;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }


    @Override
    public CreatePersonResult createPerson (final CreatePersonRequest createPersonRequest) {
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


        // -------------------------------
        if(type == PersonType.STAFF){
            return  CreatePersonResult.failure("Not supported role");
        }

        if(!emailAddress.endsWith("@hua.gr")){
            return CreatePersonResult.failure("Only academic email addresses (@hua.gr) are allowed");}

        // TODO if needed down the line

        // -------------------------------
        if(this.personRepository.existsByUsernameIgnoreCase(username)){
            return CreatePersonResult.failure("Username must be unique");        }


        if(this.personRepository.existsByEmailAddressIgnoreCase(emailAddress)){
            return CreatePersonResult.failure("Email address must be unique");
        }

        if(this.personRepository.existsByPhoneNumber(phoneNumber)){
            return CreatePersonResult.failure("Phone number must be unique");
        }

        // -------------------------------
        final PersonType personType$Lookup = this.lookupPort.lookup(username).orElse(null);
        if(personType$Lookup == null){
            return CreatePersonResult.failure("Invalid Username");
        }
        if(personType$Lookup != type){
            return CreatePersonResult.failure("The provided Person type does not match the actual one");
        }

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

        final String content=String.format("You have successfully logged in StreetFoodGo application!"+
                "Use your email (%s) to log in",emailAddress);
        final boolean sent = this.smsNotificationPort.sendSms(phoneNumber, content);
        if(!sent){
            LOGGER.warn("SMS sent to {} failed!",phoneNumber);
        }



        // Map `Person` to `PersonView`.
        // --------------------------------------------------

        final PersonView personView = this.personMapper.convertPersonToPersonView(person);

        // -------------------------------------------------

        return CreatePersonResult.success(personView);
    }
}
