package gr.hua.dit.StreetFoodGo.core.service;

import gr.hua.dit.StreetFoodGo.core.service.model.PersonView;

import java.util.List;

/**
 * Service for managing {@code Person} for data analytics purposes.
 */
public interface PersonDataService {

    List<PersonView> getAllPeople();
 }
