package com.vs.prompt.manager.web.controller;

import com.vs.prompt.manager.common.dto.PromptCreateDTO;
import com.vs.prompt.manager.common.dto.PromptDTO;
import com.vs.prompt.manager.common.dto.PromptUpdateDTO;
import com.vs.prompt.manager.common.dto.page.PromptDTOPage;
import com.vs.prompt.manager.common.mapper.PromptMapper;
import com.vs.prompt.manager.model.Category;
import com.vs.prompt.manager.model.Prompt;
import com.vs.prompt.manager.model.User;
import com.vs.prompt.manager.service.PromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing Prompts.
 */
@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/prompts")
@Tag(name = "Prompts", description = "CRUD operations for prompts")
public class PromptController {

    private final PromptService promptService;
    private final PromptMapper promptMapper;


    @Operation(
            summary = "Get all prompts (paginated)",
            description = "Returns a paginated list of all existing prompts.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (starts from 0)", example = "0"),
                    @Parameter(name = "size", description = "Number of elements per page (default 20, max 50)", example = "20"),
                    @Parameter(name = "sort", description = "Sorting criteria in the format: property(,asc|desc). Default sort is createdAt,desc", example = "createdAt,desc")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Paginated list of prompts",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PromptDTOPage.class),
                                    examples = @ExampleObject(
                                            name = "PromptPageExample",
                                            summary = "Example of paginated prompts",
                                            value = """
                    {
                      "content": [
                        {
                          "id": "a3b65c10-1a2b-4a5c-b3b6-9e5e5c20d2e1",
                          "title": "How to improve productivity",
                          "content": "Use the Pomodoro technique and prioritize tasks daily.",
                          "userId": "11111111-1111-1111-1111-111111111111",
                          "categoryId": "11111111-1111-1111-1111-000000000001",
                          "tagIds": [
                            "22222222-2222-2222-2222-000000000002",
                            "33333333-3333-3333-3333-000000000003"
                          ],
                          "createdAt": "2025-04-26T12:34:56",
                          "updatedAt": "2025-04-26T12:34:56"
                        }
                      ],
                      "totalElements": 1,
                      "totalPages": 1,
                      "number": 0,
                      "size": 20,
                      "first": true,
                      "last": true
                    }
                    """
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<PromptDTO>> findAll(@ParameterObject Pageable pageable) {
        log.info("Fetching prompts with pageable: {}", pageable);

        Page<Prompt> page = promptService.findAll(pageable);
        Page<PromptDTO> dtoPage = page.map(promptMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }







    @Operation(
            summary = "Get prompt by ID",
            description = "Returns a prompt based on the UUID provided.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Prompt found",
                            content = @Content(schema = @Schema(implementation = PromptDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Prompt not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PromptDTO> findById(
            @Parameter(description = "UUID of the prompt", required = true)
            @PathVariable UUID id) {
        log.info("Fetching prompt with id {}", id);
        Prompt prompt = promptService.findById(id);
        return ResponseEntity.ok(promptMapper.toDTO(prompt));
    }

    @Operation(
            summary = "Create a new prompt",
            description = "Creates a new prompt and returns the created entity.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Prompt created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PromptDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Validation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(
                                            name = "ValidationErrorExample",
                                            summary = "Example validation error",
                                            value = """
                    {
                      "type": "about:blank",
                      "title": "Validation Error",
                      "status": 400,
                      "detail": "Validation failed",
                      "instance": "/api/prompts",
                      "timestamp": "2025-04-26T12:45:00",
                      "errors": {
                        "title": "Title is required",
                        "content": "Content must not be blank"
                      }
                    }
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(
                                            name = "UnauthorizedExample",
                                            summary = "Example unauthorized error",
                                            value = """
                    {
                      "type": "about:blank",
                      "title": "Unauthorized",
                      "status": 401,
                      "detail": "Authentication credentials were not provided or are invalid",
                      "instance": "/api/prompts",
                      "timestamp": "2025-04-26T12:50:00"
                    }
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Not enough permissions",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(
                                            name = "ForbiddenExample",
                                            summary = "Example forbidden error",
                                            value = """
                    {
                      "type": "about:blank",
                      "title": "Forbidden",
                      "status": 403,
                      "detail": "You do not have permission to access this resource",
                      "instance": "/api/prompts",
                      "timestamp": "2025-04-26T12:52:00"
                    }
                    """
                                    )
                            )
                    )
            }
    )

    @PostMapping
    public ResponseEntity<PromptDTO> create(
            @RequestBody(
                    description = "Prompt to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PromptCreateDTO.class))
            )
            @Valid
            @org.springframework.web.bind.annotation.RequestBody PromptCreateDTO promptCreateDTO) {

        log.info("Creating new prompt: {}", promptCreateDTO.getTitle());

        Prompt prompt = promptMapper.toEntity(promptCreateDTO);

        // Set user, category and tags manually
        prompt.setUser(User.builder().id(promptCreateDTO.getUserId()).build());
        prompt.setCategory(Category.builder().id(promptCreateDTO.getCategoryId()).build());

        if (promptCreateDTO.getTagIds() != null) {
            Set<com.vs.prompt.manager.model.Tag> tags = promptCreateDTO.getTagIds().stream()
                    .map(tagId -> com.vs.prompt.manager.model.Tag.builder().id(tagId).build())
                    .collect(Collectors.toSet());
            prompt.setTags(tags);
        }

        Prompt createdPrompt = promptService.create(prompt);

        return ResponseEntity.created(URI.create("/api/prompts/" + createdPrompt.getId()))
                .body(promptMapper.toDTO(createdPrompt));
    }


    @Operation(
            summary = "Update prompt by ID",
            description = "Updates the prompt specified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Prompt updated",
                            content = @Content(schema = @Schema(implementation = PromptDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Prompt not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<PromptDTO> update(
            @Parameter(description = "UUID of the prompt to update", required = true)
            @PathVariable UUID id,
            @Valid
            @RequestBody(
                    description = "Updated prompt data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PromptUpdateDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody PromptUpdateDTO promptUpdateDTO) {

        log.info("Updating prompt with id {}", id);

        Prompt existingPrompt = promptService.findById(id);

        promptMapper.updatePromptFromDTO(promptUpdateDTO, existingPrompt);

        // Manually update category if present
        if (promptUpdateDTO.getCategoryId() != null) {
            existingPrompt.setCategory(Category.builder().id(promptUpdateDTO.getCategoryId()).build());
        }

        // Manually update tags if present
        if (promptUpdateDTO.getTagIds() != null) {
            Set<com.vs.prompt.manager.model.Tag> tags = promptUpdateDTO.getTagIds().stream()
                    .map(tagId -> com.vs.prompt.manager.model.Tag.builder().id(tagId).build())
                    .collect(Collectors.toSet());
            existingPrompt.setTags(tags);
        }

        Prompt updatedPrompt = promptService.update(id, existingPrompt);

        return ResponseEntity.ok(promptMapper.toDTO(updatedPrompt));
    }

    @Operation(
            summary = "Delete prompt by ID",
            description = "Deletes a prompt using its UUID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Prompt deleted"),
                    @ApiResponse(responseCode = "404", description = "Prompt not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID of the prompt to delete", required = true)
            @PathVariable UUID id) {
        log.info("Deleting prompt with id {}", id);
        promptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
