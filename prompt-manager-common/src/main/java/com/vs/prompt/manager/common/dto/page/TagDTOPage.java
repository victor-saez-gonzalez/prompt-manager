package com.vs.prompt.manager.common.dto.page;

import com.vs.prompt.manager.common.dto.TagDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "TagDTOPage")
public class TagDTOPage {
    @Schema(description = "Content list of tags")
    private List<TagDTO> content;

    @Schema(description = "Total number of elements")
    private long totalElements;

    @Schema(description = "Total number of pages")
    private int totalPages;

    @Schema(description = "Current page number")
    private int number;

    @Schema(description = "Page size")
    private int size;

    @Schema(description = "Is this the first page?")
    private boolean first;

    @Schema(description = "Is this the last page?")
    private boolean last;
}
