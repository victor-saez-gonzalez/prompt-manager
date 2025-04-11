package com.vs.prompt.manager.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class TagDTO {

    private UUID id;
    private String name;
    private String description;
}
