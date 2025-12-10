package gr.hua.dit.StreetFoodGo.core.service.model;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;

/**
 * CreatePersonRequest (DTO)
 */
public record CreatePersonRequest  (
        PersonType type,
        String username,
        String firstName,
        String lastName,
        String emailAddress,
        String address,
        String phoneNumber,
        String rawPassword
) {}
