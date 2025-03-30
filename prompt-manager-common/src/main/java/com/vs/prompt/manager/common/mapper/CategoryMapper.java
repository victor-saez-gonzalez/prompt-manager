package com.vs.prompt.manager.common.mapper;

import com.vs.prompt.manager.common.dto.CategoryCreateDTO;
import com.vs.prompt.manager.common.dto.CategoryDTO;
import com.vs.prompt.manager.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",  unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {


    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    CategoryDTO toDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Category toEntity(CategoryCreateDTO dto);
}
