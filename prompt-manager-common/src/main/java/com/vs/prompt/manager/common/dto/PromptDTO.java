package com.vs.prompt.manager.common.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing a Prompt for responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptDTO {

    private UUID id;
    private String title;
    private String content;
    private UUID userId;
    private UUID categoryId;
    private Set<UUID> tagIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
