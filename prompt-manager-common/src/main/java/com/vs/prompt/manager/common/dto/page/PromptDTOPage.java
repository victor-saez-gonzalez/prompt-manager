package com.vs.prompt.manager.common.dto.page;

import com.vs.prompt.manager.common.dto.PromptDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Swagger documentation wrapper for a paginated list of PromptDTOs.
 */
@Schema(name = "PromptDTOPage")
public class PromptDTOPage {

    @Schema(description = "Content list of prompts")
    private List<PromptDTO> content;

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

    // --- Getters and Setters (needed for Swagger and Jackson)

    public List<PromptDTO> getContent() {
        return content;
    }

    public void setContent(List<PromptDTO> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
