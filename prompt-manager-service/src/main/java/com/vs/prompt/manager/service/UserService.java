package com.vs.prompt.manager.service;


import com.vs.prompt.manager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    Page<User> findAll(Pageable pageable);

    User findById(UUID id);

    User create(User user);

    User update(UUID id, User updated);

    void delete(UUID id);
}
