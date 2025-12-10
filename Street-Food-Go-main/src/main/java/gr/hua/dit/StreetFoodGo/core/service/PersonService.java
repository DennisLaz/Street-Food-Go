package gr.hua.dit.StreetFoodGo.core.service;

import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonRequest;
import gr.hua.dit.StreetFoodGo.core.service.model.CreatePersonResult;

/**
 * Service (contract) for managing customers/restaurants
 */
public interface PersonService {

    CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest);
}
