package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.User;
import com.vs.prompt.manager.persistence.repository.UserRepository;
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
public class UserServiceImpl implements UserService {

    public static final String USER_NOT_FOUND_WITH_ID = "User not found with id ";
    private final UserRepository userRepository;

    @Override
    public Page<User> findAll(Pageable pageable) {
        log.info("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(UUID id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND_WITH_ID + id));
    }

    @Override
    public User create(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User update(UUID id, User updated) {
        log.info("Updating user with id: {}", id);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND_WITH_ID + id));

        if (updated.getName() != null) {
            existing.setName(updated.getName());
        }

        return userRepository.save(existing);
    }


    @Override
    public void delete(UUID id) {
        log.info("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND_WITH_ID + id));
        userRepository.delete(user);
    }
}
