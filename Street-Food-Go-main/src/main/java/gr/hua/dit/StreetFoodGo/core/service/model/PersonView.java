package gr.hua.dit.StreetFoodGo.core.service.model;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;

/**
 * PersonView (DTO) that includes only information to be exposed.
 */
public record PersonView(
        long id,
        String username,
        String firstName,
        String lastName,
        String emailAddress,
        String address,
        String phoneNumber,
        PersonType type
) {}
