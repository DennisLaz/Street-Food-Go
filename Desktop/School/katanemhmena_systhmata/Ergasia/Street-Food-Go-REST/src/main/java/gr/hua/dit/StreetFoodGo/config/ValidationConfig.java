package gr.hua.dit.StreetFoodGo.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Validation Configuration.
 */
@Configuration
public class ValidationConfig {

    @SuppressWarnings("resource")
    @Bean
    public Validator validator(){
        return Validation.buildDefaultValidatorFactory().getValidator();

}
}
