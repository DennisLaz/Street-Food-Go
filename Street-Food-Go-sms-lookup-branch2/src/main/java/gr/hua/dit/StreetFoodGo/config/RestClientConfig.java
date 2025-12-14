package gr.hua.dit.StreetFoodGo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * REST client configuration (RestTemplate bean).
 */
@Configuration
public class RestClientConfig {

    public static final String BASE_URL = "http://localhost:8081";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

