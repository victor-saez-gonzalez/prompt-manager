package com.vs.prompt.manager.web.controller;

import com.vs.prompt.manager.common.dto.CategoryCreateDTO;
import com.vs.prompt.manager.common.dto.CategoryDTO;
import com.vs.prompt.manager.common.dto.page.CategoryDTOPage;
import com.vs.prompt.manager.common.mapper.CategoryMapper;
import com.vs.prompt.manager.model.Category;
import com.vs.prompt.manager.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "CRUD operations for categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

@Operation(
        summary = "Get all categories (paginated)",
        description = "Returns a paginated list of all existing categories.",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Paginated list of categories",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CategoryDTOPage.class)
                        )
                )
        }
)
    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(@ParameterObject Pageable pageable) {

        int MAX_PAGE_SIZE = 50;
        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), MAX_PAGE_SIZE),
                pageable.getSort()
        );

        log.info("Fetching categories with pageable: {}", safePageable);
        Page<Category> page = categoryService.findAll(safePageable);
        Page<CategoryDTO> dtoPage = page.map(categoryMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(
            summary = "Get category by ID",
            description = "Returns a category based on the UUID provided.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category found",
                            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "UUID of the category", required = true)
            @PathVariable UUID id) {
        log.info("Fetching category with id {}", id);
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }


    @Operation(
            summary = "Create a new category",
            description = "Creates a new category and returns the created entity.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category created",
                            content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
            }
    )
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @RequestBody(
                    description = "Category to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryCreateDTO.class))
            )
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            CategoryCreateDTO categoryCreateDTO) {
        log.info("Creating new category: {}", categoryCreateDTO.getName());
        Category created = categoryService.create(categoryMapper.toEntity(categoryCreateDTO));
        return ResponseEntity.ok(categoryMapper.toDto(created));
    }

    @Operation(
            summary = "Update category by ID",
            description = "Updates the category specified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated",
                            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "UUID of the category to update", required = true)
            @PathVariable UUID id,
            @Valid
            @RequestBody(
                    description = "Updated category data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryCreateDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody CategoryCreateDTO categoryDto) {
        log.info("Updating category with id {}", id);
        Category updated = categoryService.update(id, categoryMapper.toEntity(categoryDto));
        return ResponseEntity.ok(categoryMapper.toDto(updated));
    }



    @Operation(
            summary = "Delete category by ID",
            description = "Deletes a category using its UUID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Category deleted"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "UUID of the category to delete", required = true)
            @PathVariable UUID id) {
        log.info("Deleting category with id {}", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
