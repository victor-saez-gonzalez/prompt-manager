package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.Category;
import com.vs.prompt.manager.persistence.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category();
        sampleCategory.setId(UUID.randomUUID());
        sampleCategory.setName("Sample");
        sampleCategory.setDescription("Sample Description");
    }

    @Test
    void shouldCreateCategorySuccessfully() {
        when(categoryRepository.save(sampleCategory)).thenReturn(sampleCategory);

        Category result = categoryService.create(sampleCategory);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Sample");
        verify(categoryRepository).save(sampleCategory);
    }

    @Test
    void shouldReturnAllCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(sampleCategory));

        when(categoryRepository.findAll(pageable)).thenReturn(page);

        Page<Category> result = categoryService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Sample");
        verify(categoryRepository).findAll(pageable);
    }


    @Test
    void shouldFindCategoryById() {
        UUID id = sampleCategory.getId();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(sampleCategory));

        Category result = categoryService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Sample");
        verify(categoryRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentCategory() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(id);
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        UUID id = sampleCategory.getId();
        Category updated = new Category();
        updated.setName("Updated Name");
        updated.setDescription("Updated Desc");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(sampleCategory)).thenReturn(sampleCategory);

        Category result = categoryService.update(id, updated);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Desc");
        verify(categoryRepository).save(sampleCategory);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
        UUID id = UUID.randomUUID();
        Category updated = new Category();
        updated.setName("New");
        updated.setDescription("New Desc");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(id, updated))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(id);
    }

    @Test
    void shouldDeleteCategorySuccessfully() {
        UUID id = sampleCategory.getId();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(sampleCategory));

        categoryService.delete(id);

        verify(categoryRepository).delete(sampleCategory);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCategory() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Category not found with id " + id);

        verify(categoryRepository).findById(id);
    }
}
