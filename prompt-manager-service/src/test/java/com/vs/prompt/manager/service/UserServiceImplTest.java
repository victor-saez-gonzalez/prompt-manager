package com.vs.prompt.manager.service;

import com.vs.prompt.manager.model.User;
import com.vs.prompt.manager.model.enums.AuthProvider;
import com.vs.prompt.manager.persistence.repository.UserRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    private static final UUID EXPECTED_UUID = UUID.randomUUID();
    private static  final String EXPECTED_EMAIL = "test@example.com";
    private static final String EXPECTED_NAME = "Test User";
    private static final AuthProvider EXPECTED_PROVIDER = AuthProvider.LOCAL;
    private static final String EXPECTED_PASSWORD = "password";

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(EXPECTED_UUID);
        user.setEmail(EXPECTED_EMAIL);
        user.setName(EXPECTED_NAME);
        user.setProvider(EXPECTED_PROVIDER);
        user.setPassword(EXPECTED_PASSWORD);
    }

    @Test
    void shouldCreateUserSuccessfully(){

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.create(user);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(EXPECTED_EMAIL);
        assertThat(result.getName()).isEqualTo(EXPECTED_NAME);
        assertThat(result.getProvider()).isEqualTo(EXPECTED_PROVIDER);
        assertThat(result.getPassword()).isEqualTo(EXPECTED_PASSWORD);
        assertThat(result.getId()).isEqualTo(EXPECTED_UUID);

    }

    @Test
    void shouldReturnAllUsers(){

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo(EXPECTED_EMAIL);
        assertThat(result.getContent().get(0).getName()).isEqualTo(EXPECTED_NAME);
        assertThat(result.getContent().get(0).getProvider()).isEqualTo(EXPECTED_PROVIDER);
        assertThat(result.getContent().get(0).getPassword()).isEqualTo(EXPECTED_PASSWORD);
        assertThat(result.getContent().get(0).getId()).isEqualTo(EXPECTED_UUID);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldFindUserById(){

        UUID id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(EXPECTED_EMAIL);
        assertThat(result.getName()).isEqualTo(EXPECTED_NAME);
        assertThat(result.getProvider()).isEqualTo(EXPECTED_PROVIDER);
        assertThat(result.getPassword()).isEqualTo(EXPECTED_PASSWORD);
        assertThat(result.getId()).isEqualTo(EXPECTED_UUID);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentUser(){

        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found with id " + id);

        verify(userRepository).findById(id);
    }

    @Test
    void shouldUpdateUserSuccessfully(){

        UUID id = user.getId();
        User updated = new User();
        updated.setName("Updated Name");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.update(id, updated);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(EXPECTED_EMAIL);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getProvider()).isEqualTo(EXPECTED_PROVIDER);
        assertThat(result.getPassword()).isEqualTo(EXPECTED_PASSWORD);
        assertThat(result.getId()).isEqualTo(EXPECTED_UUID);

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser(){

        UUID id = UUID.randomUUID();
        User updated = new User();
        updated.setName("New");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(id, updated))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found with id " + id);

        verify(userRepository).findById(id);
    }

    @Test
    void shouldDeleteUserSuccessfully(){

        UUID id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser(){

        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found with id " + id);

        verify(userRepository).findById(id);
    }

}
