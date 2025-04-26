package com.vs.prompt.manager.common.mapper;

import com.vs.prompt.manager.common.dto.PromptCreateDTO;
import com.vs.prompt.manager.common.dto.PromptDTO;
import com.vs.prompt.manager.common.dto.PromptUpdateDTO;
import com.vs.prompt.manager.model.Prompt;
import org.mapstruct.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper interface for converting between Prompt entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface PromptMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "tagIds", expression = "java(mapTagIds(prompt))")
    PromptDTO toDTO(Prompt prompt);

    Prompt toEntity(PromptDTO promptDTO);

    Prompt toEntity(PromptCreateDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePromptFromDTO(PromptUpdateDTO updateDTO, @MappingTarget Prompt prompt);

    // Helper method to map Set<Tag> to Set<UUID>
    default Set<UUID> mapTagIds(Prompt prompt) {
        if (prompt.getTags() == null) {
            return null;
        }
        return prompt.getTags().stream()
                .map(tag -> tag.getId())
                .collect(Collectors.toSet());
    }
}




//package com.vs.prompt.manager.common.mapper;




//
//import com.vs.prompt.manager.common.dto.PromptCreateDTO;
//import com.vs.prompt.manager.common.dto.PromptDTO;
//import com.vs.prompt.manager.common.dto.PromptUpdateDTO;
//import com.vs.prompt.manager.model.Prompt;
//import org.mapstruct.*;
//
//import java.util.Set;
//import java.util.UUID;
//
///**
// * Mapper interface for converting between Prompt entities and DTOs.
// */
//@Mapper(componentModel = "spring")
//public interface PromptMapper {
//
//    PromptDTO toDTO(Prompt prompt);
//
//    Prompt toEntity(PromptDTO promptDTO);
//
//    Prompt toEntity(PromptCreateDTO createDTO);
//
//    // To avoid an update set properties not sent
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updatePromptFromDTO(PromptUpdateDTO updateDTO, @MappingTarget Prompt prompt);
//
//    // Default method to map Set<UUID> to Set<Tag> if you need it later
//    default Set<UUID> mapTagsToTagIds(Prompt prompt) {
//        if (prompt.getTags() == null) {
//            return null;
//        }
//        return prompt.getTags().stream()
//                .map(tag -> tag.getId())
//                .collect(java.util.stream.Collectors.toSet());
//    }
//}
