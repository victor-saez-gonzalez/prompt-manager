package com.vs.prompt.manager.web.controller;


import com.vs.prompt.manager.common.dto.TagCreateDTO;
import com.vs.prompt.manager.common.dto.TagDTO;
import com.vs.prompt.manager.common.dto.page.TagDTOPage;
import com.vs.prompt.manager.common.mapper.TagMapper;
import com.vs.prompt.manager.model.Tag;
import com.vs.prompt.manager.model.User;
import com.vs.prompt.manager.service.TagService;
import com.vs.prompt.manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "CRUD operations for tags")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;
    private final UserService userService;

    @Operation(
            summary = "Get all tags (paginated)",
            description = "Returns a paginated list of all existing tags.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Paginated list of tags",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TagDTOPage.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<TagDTO>> getAllTags(@ParameterObject Pageable pageable) {
        log.info("Fetching all tags with pagination: {}", pageable);
        Page<Tag> tags = tagService.findAll(pageable);
        Page<TagDTO> tagDTOs = tags.map(tagMapper::toDto);
        return ResponseEntity.ok(tagDTOs);
    }

    @Operation(
            summary = "Get tag by ID",
            description = "Returns a tag based on the UUID provided.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag found",
                            content = @Content(schema = @Schema(implementation = TagDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Tag not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(
            @Parameter(description = "UUID of the tag", required = true)
            @PathVariable UUID id) {
        log.info("Fetching tag with id {}", id);
        Tag tag = tagService.findById(id);
        return ResponseEntity.ok(tagMapper.toDto(tag));
    }


    @Operation(
            summary = "Create a new tag",
                description = "Creates a new tag and returns the created entity.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tag created",
                            content = @Content(schema = @Schema(implementation = TagDTO.class)))
            }
    )
    @PostMapping
    public ResponseEntity<TagDTO> createTag(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tag to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TagCreateDTO.class))
            )
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            TagCreateDTO tagCreateDTO) {

        log.info("Creating new tag: {}", tagCreateDTO.getName());

        User user = userService.findById(tagCreateDTO.getUserId());

        Tag tag = tagService.create(tagMapper.toEntity(tagCreateDTO, user));

        return ResponseEntity.created(URI.create("/tags/" + tag.getId()))
                .body(tagMapper.toDto(tag));
    }

    @Operation(
            summary = "Update tag by ID",
            description = "Updates the tag specified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag updated",
                            content = @Content(schema = @Schema(implementation = TagDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Tag not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @Parameter(description = "UUID of the tag to update", required = true)
            @PathVariable UUID id,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated tag data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TagCreateDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody TagCreateDTO tagDto) {

        log.info("Updating tag with id {}", id);

        User user = userService.findById(tagDto.getUserId());

        Tag tag = tagMapper.toEntity(tagDto, user);

        Tag updated = tagService.update(id, tag);

        return ResponseEntity.ok(tagMapper.toDto(updated));
    }



    @Operation(
            summary = "Delete tag by ID",
            description = "Deletes a tag using its UUID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tag deleted"),
                    @ApiResponse(responseCode = "404", description = "Tag not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @Parameter(description = "UUID of the tag to delete", required = true)
            @PathVariable UUID id) {
        log.info("Deleting tag with id {}", id);
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
