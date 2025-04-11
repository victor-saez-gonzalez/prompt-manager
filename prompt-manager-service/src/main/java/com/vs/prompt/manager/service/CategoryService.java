package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {

    Page<Category> findAll(Pageable pageable);

    Category findById(UUID id);

    Category create(Category category);

    Category update(UUID id, Category category);

    void delete(UUID id);
}
