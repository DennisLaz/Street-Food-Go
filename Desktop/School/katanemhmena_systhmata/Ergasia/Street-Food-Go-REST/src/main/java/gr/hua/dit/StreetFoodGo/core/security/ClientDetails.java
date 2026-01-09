package gr.hua.dit.StreetFoodGo.core.security;

import java.util.Set;

/**
 * Client details POJO
 */
public record ClientDetails(
    String id,
    String secret,
    Set<String> roles
) {}
