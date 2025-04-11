package com.vs.prompt.manager.persistence.repository;

import com.vs.prompt.manager.model.Category;
import com.vs.prompt.manager.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TagRepository  extends JpaRepository<Tag, UUID> {
}
