package com.vs.prompt.manager.common.dto;

import com.vs.prompt.manager.model.enums.AuthProvider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {

    @NotBlank
    @Email(message = "Email should be valid")
    String email;

    @Size(max = 100 , message = "Name cannot exceed 100 characters")
    String name;

    String password; // nullable if external authentication

    @NotNull AuthProvider provider;

    /**
     * Unique identifier of the user provided by the external authentication provider
     * (e.g., Google, GitHub). Used to associate the user account with the third-party service.
     * Can be null for users authenticated via LOCAL provider.
     */
    String providerId;


}
