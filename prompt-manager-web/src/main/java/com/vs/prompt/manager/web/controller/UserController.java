package com.vs.prompt.manager.web.controller;


import com.vs.prompt.manager.common.dto.UserCreateDTO;
import com.vs.prompt.manager.common.dto.UserDTO;
import com.vs.prompt.manager.common.mapper.UserMapper;
import com.vs.prompt.manager.model.User;
import com.vs.prompt.manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "CRUD operations for users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @Operation(
            summary = "Get all users (paginated)",
            description = "Returns a paginated list of all existing users.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Paginated list of users",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(@ParameterObject Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        Page<UserDTO> userDTOs = users.map(userMapper::toDto);
        return ResponseEntity.ok(userDTOs);
    }


    @Operation(
            summary = "Get user by ID",
            description = "Returns a user based on the UUID provided.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        User user = userService.findById(id);
        UserDTO userDTO = userMapper.toDto(user);
        return ResponseEntity.ok(userDTO);
    }


    @Operation(
            summary = "Create a new user",
            description = "Creates a new user and returns the created entity.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created",
                            content = @Content(schema = @Schema(implementation = UserDTO.class)))
            }
    )
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid  @RequestBody UserCreateDTO userDTO) {

        User user = userMapper.toEntity(userDTO);
        User createdUser = userService.create(user);
        UserDTO createdUserDTO = userMapper.toDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDTO);
    }

    @Operation(
            summary = "Update user by ID",
            description = "Updates the user specified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserCreateDTO userDTO) {

        User user = userMapper.toEntity(userDTO);
        User updatedUser = userService.update(id, user);
        UserDTO updatedUserDTO = userMapper.toDto(updatedUser);
        return ResponseEntity.ok(updatedUserDTO);
    }


    @Operation(
            summary = "Delete user by ID",
            description = "Deletes a user using its UUID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
