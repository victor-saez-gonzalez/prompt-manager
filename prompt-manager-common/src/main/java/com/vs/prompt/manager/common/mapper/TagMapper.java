package com.vs.prompt.manager.common.mapper;

import com.vs.prompt.manager.common.dto.TagCreateDTO;
import com.vs.prompt.manager.common.dto.TagDTO;
import com.vs.prompt.manager.model.Tag;
import com.vs.prompt.manager.model.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",  unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TagMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    TagDTO toDto(Tag tag);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "user", ignore = true)
    Tag toEntity(TagCreateDTO dto , @Context Object ignored);

    default Tag toEntity(TagCreateDTO dto, User user) {
        Tag tag = toEntity(dto, (Object) user);
        tag.setUser(user);
        return tag;
    }
}
