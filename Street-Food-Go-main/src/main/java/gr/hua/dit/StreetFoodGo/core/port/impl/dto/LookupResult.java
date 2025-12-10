package gr.hua.dit.StreetFoodGo.core.port.impl.dto;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;

/**
 * LookupResult DTO.
 */
public record LookupResult(
        String raw,
        boolean exists,
        String huaId,
        PersonType type
) {}