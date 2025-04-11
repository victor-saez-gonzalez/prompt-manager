package com.vs.prompt.manager.service;


import com.vs.prompt.manager.model.Tag;
import com.vs.prompt.manager.persistence.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag sampleTag;

    @BeforeEach
    void setUp() {
        sampleTag = new Tag();
        sampleTag.setId(UUID.randomUUID());
        sampleTag.setName("Sample");
        sampleTag.setDescription("Sample Description");
    }

    @Test
    void shouldCreateTagSuccessfully() {

        when(tagRepository.save(sampleTag)).thenReturn(sampleTag);

        Tag result = tagService.create(sampleTag);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Sample");
        verify(tagRepository).save(sampleTag);
    }

    @Test
    void shouldReturnAllTags() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> page = new PageImpl<>(List.of(sampleTag));

        when(tagRepository.findAll(pageable)).thenReturn(page);

        Page<Tag> result = tagService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Sample");
        verify(tagRepository).findAll(pageable);

    }

    @Test
    void shouldReturnTagById() {
        UUID id = sampleTag.getId();

        when(tagRepository.findById(sampleTag.getId())).thenReturn(java.util.Optional.of(sampleTag));

        Tag result = tagService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Sample");
        verify(tagRepository).findById(sampleTag.getId());
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentTag() {
        UUID id = UUID.randomUUID();

        when(tagRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> tagService.findById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Tag not found");

        verify(tagRepository).findById(id);
    }

    @Test
    void shouldUpdateTagSuccessfully() {
        UUID id = sampleTag.getId();
        Tag updatedTag = new Tag();
        updatedTag.setName("Updated Name");
        updatedTag.setDescription("Updated Description");

        when(tagRepository.findById(id)).thenReturn(java.util.Optional.of(sampleTag));
        when(tagRepository.save(sampleTag)).thenReturn(sampleTag);

        Tag result = tagService.update(id, updatedTag);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(tagRepository).findById(id);
        verify(tagRepository).save(sampleTag);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTag() {
        UUID id = UUID.randomUUID();
        Tag updatedTag = new Tag();
        updatedTag.setName("Updated Name");
        updatedTag.setDescription("Updated Description");

        when(tagRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> tagService.update(id, updatedTag))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Tag not found");

        verify(tagRepository).findById(id);
    }

    @Test
    void shouldDeleteTagSuccessfully() {
        UUID id = sampleTag.getId();

        when(tagRepository.findById(id)).thenReturn(java.util.Optional.of(sampleTag));

        tagService.delete(id);

        verify(tagRepository).findById(id);
        verify(tagRepository).delete(sampleTag);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTag() {
        UUID id = UUID.randomUUID();

        when(tagRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> tagService.delete(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Tag not found with id " + id);

        verify(tagRepository).findById(id);
    }
}
