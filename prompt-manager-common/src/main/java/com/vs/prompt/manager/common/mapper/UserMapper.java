package com.vs.prompt.manager.common.mapper;

import com.vs.prompt.manager.common.dto.UserCreateDTO;
import com.vs.prompt.manager.common.dto.UserDTO;
import com.vs.prompt.manager.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreateDTO dto);
}
