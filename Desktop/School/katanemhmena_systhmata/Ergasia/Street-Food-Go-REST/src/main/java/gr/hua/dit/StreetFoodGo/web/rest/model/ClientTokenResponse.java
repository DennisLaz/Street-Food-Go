package gr.hua.dit.StreetFoodGo.web.rest.model;

import gr.hua.dit.StreetFoodGo.web.rest.ClientAuthResource;

/**
 * @see ClientAuthResource
 */
public record ClientTokenResponse(
    String accessToken,
    String tokenType,
    long expiresIn
) {}
