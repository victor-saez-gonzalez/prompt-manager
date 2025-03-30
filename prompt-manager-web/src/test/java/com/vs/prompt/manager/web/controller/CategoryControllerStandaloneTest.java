package com.vs.prompt.manager.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vs.prompt.manager.common.dto.CategoryCreateDTO;
import com.vs.prompt.manager.common.dto.CategoryDTO;
import com.vs.prompt.manager.common.mapper.CategoryMapper;
import com.vs.prompt.manager.web.exception.GlobalExceptionHandler;
import com.vs.prompt.manager.model.Category;
import com.vs.prompt.manager.persistence.repository.CategoryRepository;
import com.vs.prompt.manager.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerStandaloneTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryController categoryController;

    private final ObjectMapper objectMapper = new ObjectMapper();

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(categoryController)
//                .setControllerAdvice(new GlobalExceptionHandler())
//                .build();
//    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setCustomArgumentResolvers(pageableResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setDescription("No name");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.name").value("Name is mandatory"));
    }

    @Test
    void shouldCreateCategorySuccessfully() throws Exception {
        CategoryCreateDTO requestDto = new CategoryCreateDTO("Standalone", "Test");
        UUID id = UUID.randomUUID();

        CategoryDTO responseDto = new CategoryDTO();
        responseDto.setId(id);
        responseDto.setName("Standalone");
        responseDto.setDescription("Test");

        when(categoryService.create(any(Category.class))).thenReturn(new Category());
        when(categoryMapper.toDto(any(Category.class))).thenReturn(responseDto);
        when(categoryMapper.toEntity(any(CategoryCreateDTO.class))).thenReturn(new Category());

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Standalone"))
                .andExpect(jsonPath("$.description").value("Test"));
    }

    @Test
    void shouldUpdateCategorySuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryCreateDTO updateDto = new CategoryCreateDTO("Updated Name", "Updated Description");

        CategoryDTO updatedResponse = new CategoryDTO();
        updatedResponse.setId(id);
        updatedResponse.setName(updateDto.getName());
        updatedResponse.setDescription(updateDto.getDescription());

        when(categoryService.update(any(UUID.class), any(Category.class))).thenReturn(new Category());
        when(categoryMapper.toDto(any(Category.class))).thenReturn(updatedResponse);
        when(categoryMapper.toEntity(any(CategoryCreateDTO.class))).thenReturn(new Category());

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/categories/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }
    @Test
    void shouldFailUpdateWithInvalidName() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryCreateDTO invalidDto = new CategoryCreateDTO("", "Has description");

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/categories/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.name").value("Name is mandatory"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingCategory() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryCreateDTO updateDto = new CategoryCreateDTO("Does not exist", "Trying to update non-existent category");

        when(categoryMapper.toEntity(any(CategoryCreateDTO.class))).thenReturn(new Category());
        when(categoryService.update(any(UUID.class), any(Category.class)))
                .thenThrow(new NoSuchElementException("Category not found with id " + id));

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/categories/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Category not found with id " + id));
    }

    @Test
    void shouldDeleteCategorySuccessfully() throws Exception {
        UUID id = UUID.randomUUID();


        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/categories/" + id)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingCategory() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new NoSuchElementException("Category not found with id " + id))
                .when(categoryService).delete(id);

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/categories/" + id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Category not found with id " + id));
    }
    @Test
    void shouldReturnCategoryByIdSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        Category category = new Category();
        category.setId(id);
        category.setName("Get Me");
        category.setDescription("Return this");

        CategoryDTO dto = new CategoryDTO();
        dto.setId(id);
        dto.setName("Get Me");
        dto.setDescription("Return this");

        when(categoryService.findById(id)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/categories/" + id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Get Me"))
                .andExpect(jsonPath("$.description").value("Return this"));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingCategory() throws Exception {
        UUID id = UUID.randomUUID();

        when(categoryService.findById(id))
                .thenThrow(new NoSuchElementException("Category not found"));

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/categories/" + id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Category not found"));
    }


    @Test
    void shouldThrowExceptionWhenFindingNonExistentCategory() {
        UUID id = UUID.randomUUID();

        when(categoryService.findById(id))
                .thenThrow(new NoSuchElementException("Category not found"));

        assertThatThrownBy(() -> categoryController.getCategoryById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Category not found");

        verify(categoryService).findById(id);
    }



//    @Test
//    void shouldReturnListOfCategories() throws Exception {
//        UUID id1 = UUID.randomUUID();
//        UUID id2 = UUID.randomUUID();
//
//        Category category1 = new Category();
//        category1.setId(id1);
//        category1.setName("Category A");
//        category1.setDescription("Desc A");
//
//        Category category2 = new Category();
//        category2.setId(id2);
//        category2.setName("Category B");
//        category2.setDescription("Desc B");
//
//        CategoryDTO dto1 = new CategoryDTO();
//        dto1.setId(id1);
//        dto1.setName("Category A");
//        dto1.setDescription("Desc A");
//
//        CategoryDTO dto2 = new CategoryDTO();
//        dto2.setId(id2);
//        dto2.setName("Category B");
//        dto2.setDescription("Desc B");
//
//        when(categoryService.findAll()).thenReturn(List.of(category1, category2));
//        when(categoryMapper.toDto(category1)).thenReturn(dto1);
//        when(categoryMapper.toDto(category2)).thenReturn(dto2);
//
//        mockMvc.perform(
//                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/categories")
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].id").value(id1.toString()))
//                .andExpect(jsonPath("$[0].name").value("Category A"))
//                .andExpect(jsonPath("$[1].id").value(id2.toString()))
//                .andExpect(jsonPath("$[1].name").value("Category B"));
//    }

    @Test
    void shouldReturnListOfCategories() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Category category1 = new Category();
        category1.setId(id1);
        category1.setName("Category A");
        category1.setDescription("Desc A");

        Category category2 = new Category();
        category2.setId(id2);
        category2.setName("Category B");
        category2.setDescription("Desc B");

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setId(id1);
        dto1.setName("Category A");
        dto1.setDescription("Desc A");

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setId(id2);
        dto2.setName("Category B");
        dto2.setDescription("Desc B");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category1, category2), pageable, 2);

        when(categoryService.findAll(any(Pageable.class))).thenReturn(categoryPage);
        when(categoryMapper.toDto(category1)).thenReturn(dto1);
        when(categoryMapper.toDto(category2)).thenReturn(dto2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(id1.toString()))
                .andExpect(jsonPath("$.content[0].name").value("Category A"))
                .andExpect(jsonPath("$.content[1].id").value(id2.toString()))
                .andExpect(jsonPath("$.content[1].name").value("Category B"));
    }



}
