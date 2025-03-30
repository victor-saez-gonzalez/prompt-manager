package com.vs.prompt.manager.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

/**
 * Data Transfer Object for Category.
 */
@Data
public class CategoryDTO {
    private UUID id;
    private String name;
    private String description;
}

