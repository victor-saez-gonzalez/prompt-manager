package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.Category;
import com.vs.prompt.manager.persistence.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Override
    public Page<Category> findAll(Pageable pageable) {
        log.info("Fetching all categories with pagination: {}", pageable);
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category findById(UUID id) {
        log.info("Fetching category with id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
    }


    @Override
    public Category create(Category category) {
        log.info("Creating category with name: {}", category.getName());
        return categoryRepository.save(category);
    }

    @Override
    public Category update(UUID id, Category updated) {
        log.info("Updating category with id: {}", id);
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return categoryRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id " + id));
        categoryRepository.delete(category);
    }
}
