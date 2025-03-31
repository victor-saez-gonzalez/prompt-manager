package com.vs.prompt.manager.common.dto;

import com.vs.prompt.manager.model.enums.AuthProvider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateDTO(
        @NotBlank @Email String email,
        String name,
        String password, // nullable if external authentication
        @NotNull AuthProvider provider,
        String providerId
) {}
