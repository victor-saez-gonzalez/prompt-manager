package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.Prompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing Prompts.
 */
public interface PromptService {

    Page<Prompt> findAll(Pageable pageable);

    Prompt findById(UUID id);

    Prompt create(Prompt prompt);

    Prompt update(UUID id, Prompt prompt);

    void delete(UUID id);
}
