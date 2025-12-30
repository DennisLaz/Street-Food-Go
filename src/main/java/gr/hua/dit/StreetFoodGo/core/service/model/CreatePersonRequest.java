package gr.hua.dit.StreetFoodGo.core.service.model;

import gr.hua.dit.StreetFoodGo.core.model.PersonType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CreatePersonRequest (DTO)
 */
public record CreatePersonRequest  (
        @NotNull PersonType type,
        @NotNull @NotBlank @Size(max=20) String username,
        @NotNull @NotBlank @Size(max=20) String firstName,
        @NotNull @NotBlank @Size(max=20) String lastName,
        @NotNull @NotBlank @Size(max=100) @Email String emailAddress,
        @NotNull @NotBlank @Size(max=100)  String address,
        @NotNull @NotBlank @Size(max=18) String phoneNumber,
        @NotNull @NotBlank @Size(min = 4 , max=24) String rawPassword
) {}
