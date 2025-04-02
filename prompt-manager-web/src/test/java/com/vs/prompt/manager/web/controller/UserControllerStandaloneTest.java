package com.vs.prompt.manager.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vs.prompt.manager.common.dto.UserCreateDTO;
import com.vs.prompt.manager.common.dto.UserDTO;
import com.vs.prompt.manager.common.mapper.UserMapper;
import com.vs.prompt.manager.model.User;
import com.vs.prompt.manager.model.enums.AuthProvider;
import com.vs.prompt.manager.persistence.repository.UserRepository;
import com.vs.prompt.manager.service.UserService;
import com.vs.prompt.manager.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerStandaloneTest {

    private MockMvc mockMvc;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String EXPECTED_EMAIL = "test@example.com";
    private final String EXPECTED_NAME = "John Doe";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setCustomArgumentResolvers(pageableResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnBadRequestWhenCreateUserWithBlankEmail() throws Exception {
        // given
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("John Doe");
        userCreateDTO.setProvider(AuthProvider.LOCAL);

        // when
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.instance").value("/api/users"))
                .andExpect(jsonPath("$.errors.email").value("must not be blank"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setProvider(AuthProvider.LOCAL);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setProvider(AuthProvider.LOCAL);

        when(userService.create(any(User.class))).thenReturn(new User());
        when(userMapper.toDto(any(User.class))).thenReturn(userDTO);
        when(userMapper.toEntity(any(UserCreateDTO.class))).thenReturn(new User());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.provider").value("LOCAL"));

    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        UserCreateDTO updateDto = new UserCreateDTO();
        updateDto.setEmail(EXPECTED_EMAIL);
        updateDto.setName(EXPECTED_NAME);
        updateDto.setProvider(AuthProvider.LOCAL);


        UserDTO updatedResponse = new UserDTO();
        updatedResponse.setId(id);
        updatedResponse.setName(EXPECTED_NAME);
        updatedResponse.setEmail(EXPECTED_EMAIL);
        updatedResponse.setProvider(AuthProvider.LOCAL);

        when(userService.update(any(UUID.class), any(User.class))).thenReturn(new User());
        when(userMapper.toDto(any(User.class))).thenReturn(updatedResponse);
        when(userMapper.toEntity(any(UserCreateDTO.class))).thenReturn(new User());

        mockMvc.perform(put("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(EXPECTED_NAME))
                .andExpect(jsonPath("$.email").value(EXPECTED_EMAIL));

    }
    @Test
    void shouldFailUpdateWIthInvalidEmail() throws Exception{

        UUID id = UUID.randomUUID();

        UserCreateDTO invalidDto = new UserCreateDTO();
        invalidDto.setEmail("invalid-email");

        mockMvc.perform(put("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.email").value("Email should be valid"));

    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNotExistingUser() throws Exception {

        UUID id = UUID.randomUUID();

        UserCreateDTO updateDto = new UserCreateDTO();
        updateDto.setEmail("not_existing@example.com");
        updateDto.setProvider(AuthProvider.LOCAL);

        when(userMapper.toEntity(any(UserCreateDTO.class))).thenReturn(new User());
        when(userService.update(any(UUID.class), any(User.class))).thenThrow(new NoSuchElementException("User not found with id " + id));

        mockMvc.perform(put("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("User not found with id " + id))
                .andExpect(jsonPath("$.instance").value("/api/users/" + id))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingUser() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new NoSuchElementException("User not found with id " + id))
                .when(userService).delete(id);

        mockMvc.perform(
                        delete("/api/users/" + id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("User not found with id " + id));
    }
    @Test
    void shouldReturnUserByIdSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        User user = new User();
        user.setId(id);
        user.setName("Get Me");
        user.setEmail("Return this");

        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setName("Get Me");
        dto.setEmail("Return this");

        when(userService.findById(id)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(
                        get("/api/users/" + id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Get Me"))
                .andExpect(jsonPath("$.email").value("Return this"));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingUser() throws Exception {
        UUID id = UUID.randomUUID();

        when(userService.findById(id))
                .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(
                        get("/api/users/" + id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("User not found"));
    }


    @Test
    void shouldThrowExceptionWhenFindingNonExistentUser() {
        UUID id = UUID.randomUUID();

        when(userService.findById(id))
                .thenThrow(new NoSuchElementException("User not found"));

        assertThatThrownBy(() -> userController.getUserById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");

        verify(userService).findById(id);
    }

    @Test
    void shouldReturnListOfUsers() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        User user1 = new User();
        user1.setId(id1);
        user1.setName("User A");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(id2);
        user2.setName("User B");
        user2.setEmail("user2@example.com");

        UserDTO dto1 = new UserDTO();
        dto1.setId(id1);
        dto1.setName("User A");
        dto1.setEmail("user1@example.com");

        UserDTO dto2 = new UserDTO();
        dto2.setId(id2);
        dto2.setName("User B");
        dto2.setEmail("user2@example.com");

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userService.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(id1.toString()))
                .andExpect(jsonPath("$.content[0].name").value("User A"))
                .andExpect(jsonPath("$.content[1].id").value(id2.toString()))
                .andExpect(jsonPath("$.content[1].name").value("User B"));
    }


}
