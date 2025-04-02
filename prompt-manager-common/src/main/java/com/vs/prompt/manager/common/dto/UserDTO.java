package com.vs.prompt.manager.common.dto;



import com.vs.prompt.manager.model.enums.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO{

    UUID id;
    String email;
    String name;
    AuthProvider provider;
}
