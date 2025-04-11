package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.Tag;
import com.vs.prompt.manager.persistence.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public Page<Tag> findAll(Pageable pageable) {
        log.info("Fetching all tags with pagination: {}", pageable);
        return tagRepository.findAll(pageable);
    }

    @Override
    public Tag findById(UUID id) {
        log.info("Fetching tag with id: {}", id);
        return tagRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tag not found"));
    }

    @Override
    public Tag create(Tag tag) {
        log.info("Creating tag with name: {}", tag.getName());
        return tagRepository.save(tag);
    }

    @Override
    public Tag update(UUID id, Tag tag) {
        log.info("Updating category with id: {}", id);
        Tag existing = tagRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tag not found"));
        existing.setName(tag.getName());
        existing.setDescription(tag.getDescription());
        return tagRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting tag with id: {}", id);
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tag not found with id " + id));
        tagRepository.delete(tag);
    }
}
