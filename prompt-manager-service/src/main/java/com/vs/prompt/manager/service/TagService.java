package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.Tag;
import com.vs.prompt.manager.persistence.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TagService {

    Page<Tag> findAll(Pageable pageable);

    Tag findById(UUID id);

    Tag create(Tag tag);

    Tag update(UUID id, Tag tag);

    void delete(UUID id);

}
