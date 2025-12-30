package gr.hua.dit.StreetFoodGo.core.security;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;

/**
 * @see CurrentUserProvider
 */
public record CurrentUser (long id, String username,String emailAddress, PersonType type) {}
