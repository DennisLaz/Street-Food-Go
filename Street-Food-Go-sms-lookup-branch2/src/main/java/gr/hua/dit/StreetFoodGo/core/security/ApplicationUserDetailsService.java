package gr.hua.dit.StreetFoodGo.core.security;

import gr.hua.dit.StreetFoodGo.core.repository.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of String's {@link UserDetailsService} for providing application users
 */
@Service
public final class ApplicationUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public ApplicationUserDetailsService(final PersonRepository personRepository) {
        if (personRepository == null) throw new NullPointerException();
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();

        return this.personRepository.findByEmailAddressIgnoreCase(username.strip())
                .map(person -> {
                    return new ApplicationUserDetails(
                            person.getId(),
                            person.getUsername(),
                            person.getEmailAddress(),
                            person.getPasswordHash(),
                            person.getType());
                })
                .orElseThrow(() -> new UsernameNotFoundException("User" + username + " not found"));
    }
}
