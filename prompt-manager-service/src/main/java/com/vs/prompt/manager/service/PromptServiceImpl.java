package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.Category;
import com.vs.prompt.manager.model.Prompt;

import com.vs.prompt.manager.model.Tag;
import com.vs.prompt.manager.persistence.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of PromptService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromptServiceImpl implements PromptService {

    private final PromptRepository promptRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    @Override
    public Page<Prompt> findAll(Pageable pageable) {
        log.info("Fetching paginated prompts: {}", pageable);
        return promptRepository.findAll(pageable);
    }

    @Override
    public Prompt findById(UUID id) {
        log.info("Retrieving prompt with id: {}", id);
        return promptRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Prompt not found with id: {}", id);
                    return new RuntimeException("Prompt not found with id: " + id);
                });
    }

    @Override
    public Prompt create(Prompt prompt) {
        log.info("Creating new prompt: {}", prompt.getTitle());
        validateOwnership(prompt);
        return promptRepository.save(prompt);
    }

    @Override
    public Prompt update(UUID id, Prompt prompt) {
        log.info("Updating prompt with id: {}", id);
        validateOwnership(prompt);

        Prompt existingPrompt = findById(id);

        existingPrompt.setTitle(prompt.getTitle());
        existingPrompt.setContent(prompt.getContent());
        existingPrompt.setCategory(prompt.getCategory());
        existingPrompt.setUser(prompt.getUser());
        existingPrompt.setTags(prompt.getTags());

        return promptRepository.save(existingPrompt);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting prompt with id: {}", id);
        promptRepository.deleteById(id);
    }



    /**
     * Validates that the user owns the category and all tags associated with the prompt.
     */
    private void validateOwnership(Prompt prompt) {
        UUID userId = prompt.getUser().getId();

        // Validate Category ownership
        if (prompt.getCategory() != null) {
            Category category = categoryService.findById(prompt.getCategory().getId());
            if (!category.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("The category does not belong to the user.");
            }
        }

        // Validate Tags ownership
        if (prompt.getTags() != null) {
            for (Tag tag : prompt.getTags()) {
                Tag foundTag = tagService.findById(tag.getId());
                if (!foundTag.getUser().getId().equals(userId)) {
                    throw new IllegalArgumentException("One or more tags do not belong to the user.");
                }
            }
        }
    }

}
