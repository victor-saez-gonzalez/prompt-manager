package com.vs.prompt.manager.persistence.repository;

import com.vs.prompt.manager.model.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 * Repository interface for managing Prompt entities.
 */
public interface PromptRepository extends JpaRepository<Prompt, UUID> {
    // Define custom query methods here if needed
}
