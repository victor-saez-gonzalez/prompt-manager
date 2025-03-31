package com.vs.prompt.manager.common.dto;



import com.vs.prompt.manager.model.enums.AuthProvider;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        String name,
        AuthProvider provider
) {}
