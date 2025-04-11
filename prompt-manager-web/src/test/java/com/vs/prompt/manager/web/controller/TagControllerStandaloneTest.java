package com.vs.prompt.manager.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vs.prompt.manager.common.dto.TagCreateDTO;
import com.vs.prompt.manager.common.dto.TagDTO;
import com.vs.prompt.manager.common.mapper.TagMapper;
import com.vs.prompt.manager.model.Tag;
import com.vs.prompt.manager.model.User;
import com.vs.prompt.manager.service.TagService;
import com.vs.prompt.manager.service.UserService;
import com.vs.prompt.manager.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
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

class TagControllerStandaloneTest {

    private MockMvc mockMvc;
    @Mock
    private TagService tagService;
    @Mock
    private TagMapper tagMapper;
    @Mock
    private UserService userService;
    @Mock
    private Environment environment;

    @InjectMocks
    private TagController tagController;

    private static final String BASE_URL = "/api/tags";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(environment.matchesProfiles("development")).thenReturn(true);

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders.standaloneSetup(tagController)
                .setCustomArgumentResolvers(pageableResolver)
                .setControllerAdvice(new GlobalExceptionHandler(environment))
                .build();
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
        TagCreateDTO tagCreateDTO = new TagCreateDTO();
        tagCreateDTO.setDescription("No name");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.name").value("Name is mandatory"));
    }

    @Test
    void shouldCreateTagSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();
        TagCreateDTO tagCreateDTO = new TagCreateDTO("Test Tag", "This is a test tag", userId);
        UUID id = UUID.randomUUID();

        TagDTO responseDto = new TagDTO();
        responseDto.setId(id);
        responseDto.setName(tagCreateDTO.getName());
        responseDto.setDescription(tagCreateDTO.getDescription());

        when(tagService.create(any(Tag.class))).thenReturn(new Tag());
        when(tagMapper.toDto(any(Tag.class))).thenReturn(responseDto);
        when(tagMapper.toEntity(any(TagCreateDTO.class), any(User.class))).thenReturn(new Tag());
        when(userService.findById(userId)).thenReturn(new User());


        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Test Tag"))
                .andExpect(jsonPath("$.description").value("This is a test tag"));
    }

    @Test
    void shouldUpdateTagSuccessfully() throws Exception  {

        UUID userId = UUID.randomUUID();
        TagCreateDTO tagCreateDTO = new TagCreateDTO("Updated Tag", "This is an updated tag", userId);

        UUID id = UUID.randomUUID();
        TagDTO responseDto = new TagDTO(id,tagCreateDTO.getName(), tagCreateDTO.getDescription());

        when(tagService.update(any(UUID.class), any(Tag.class))).thenReturn(new Tag());
        when(tagMapper.toDto(any(Tag.class))).thenReturn(responseDto);
        when(tagMapper.toEntity(any(TagCreateDTO.class), any(User.class))).thenReturn(new Tag());
        when(userService.findById(userId)).thenReturn(new User());

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Updated Tag"))
                .andExpect(jsonPath("$.description").value("This is an updated tag"));
    }

    @Test
    void shouldFailUpdateWithInvalidName() throws Exception{

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        // Create a TagCreateDTO with an empty name
        TagCreateDTO invalidDTO = new TagCreateDTO("", "This is an updated tag", userId);

        mockMvc.perform(put(BASE_URL + "/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.name").value("Name is mandatory"));

    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNotExistingTag() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TagCreateDTO tagCreateDTO = new TagCreateDTO("Updated Tag", "This is an updated tag", userId);

        User mockUser = new User();
        mockUser.setId(userId);

        Tag mappedEntity = new Tag();
        mappedEntity.setName(tagCreateDTO.getName());
        mappedEntity.setDescription(tagCreateDTO.getDescription());
        mappedEntity.setUser(mockUser);

        when(tagMapper.toEntity(tagCreateDTO, mockUser)).thenReturn(mappedEntity);

        when(tagService.update(id,mappedEntity)).thenThrow(new NoSuchElementException("Tag not found"));
        when(userService.findById(userId)).thenReturn(mockUser);

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagCreateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void shouldDeleteTagSuccessfully() throws Exception {
        mockMvc.perform(
                        delete(BASE_URL + "/{id}",  UUID.randomUUID())
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingTag() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new NoSuchElementException("Tag not found with id " + id))
                .when(tagService).delete(id);

        mockMvc.perform(
                        delete(BASE_URL + "/" + id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void shouldReturnTagByIdSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        Tag tag = new Tag();
        tag.setId(id);
        tag.setName("Get Me");
        tag.setDescription("Return this");

        TagDTO dto = new TagDTO();
        dto.setId(id);
        dto.setName("Get Me");
        dto.setDescription("Return this");

        when(tagService.findById(id)).thenReturn(tag);
        when(tagMapper.toDto(tag)).thenReturn(dto);

        mockMvc.perform(
                        get(BASE_URL +"/" + id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Get Me"))
                .andExpect(jsonPath("$.description").value("Return this"));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingTag() throws Exception {
        UUID id = UUID.randomUUID();

        when(tagService.findById(id))
                .thenThrow(new NoSuchElementException("Tag not found"));

        mockMvc.perform(
                        get(BASE_URL + "/" + id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Tag not found"));
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentTag() {
        UUID id = UUID.randomUUID();

        when(tagService.findById(id))
                .thenThrow(new NoSuchElementException("Tag not found"));

        assertThatThrownBy(() -> tagController.getTagById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Tag not found");

        verify(tagService).findById(id);
    }

    @Test
    void shouldReturnListOfCategories() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Tag tag1 = new Tag();
        tag1.setId(id1);
        tag1.setName("Tag A");
        tag1.setDescription("Desc A");

        Tag tag2 = new Tag();
        tag2.setId(id2);
        tag2.setName("Tag B");
        tag2.setDescription("Desc B");

        TagDTO dto1 = new TagDTO();
        dto1.setId(id1);
        dto1.setName("Tag A");
        dto1.setDescription("Desc A");

        TagDTO dto2 = new TagDTO();
        dto2.setId(id2);
        dto2.setName("Tag B");
        dto2.setDescription("Desc B");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tagPage = new PageImpl<>(List.of(tag1, tag2), pageable, 2);

        when(tagService.findAll(any(Pageable.class))).thenReturn(tagPage);
        when(tagMapper.toDto(tag1)).thenReturn(dto1);
        when(tagMapper.toDto(tag2)).thenReturn(dto2);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL  + "?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(id1.toString()))
                .andExpect(jsonPath("$.content[0].name").value("Tag A"))
                .andExpect(jsonPath("$.content[1].id").value(id2.toString()))
                .andExpect(jsonPath("$.content[1].name").value("Tag B"));
    }



}
