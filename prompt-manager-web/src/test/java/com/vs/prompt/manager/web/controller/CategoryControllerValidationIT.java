package com.vs.prompt.manager.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vs.prompt.manager.common.dto.CategoryCreateDTO;
import com.vs.prompt.manager.common.dto.CategoryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryControllerValidationIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getUrl() {
        return "http://localhost:" + port + "/api/categories";
    }


    @Test
    void shouldFailWhenNameIsMissingUsingObjectMapper() throws Exception {
        // Arrange
        CategoryDTO category = new CategoryDTO();
        category.setDescription("No name provided");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CategoryDTO> request = new HttpEntity<>(category, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                request,
                String.class
        );

        // Assert basic
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        String body = response.getBody();
        assertThat(body).isNotNull();

        // Parse with Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(body);

        // Basic checks
        assertThat(root.get("title").asText()).isEqualTo("Validation Error");
        assertThat(root.get("detail").asText()).contains("Validation failed");

        // Nested errors
        JsonNode errors = root.get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors.has("name")).isTrue();
        assertThat(errors.get("name").asText()).contains("Name is mandatory");

        // Timestamp and instance
        assertThat(root.get("timestamp")).isNotNull();
        assertThat(root.get("instance")).isNotNull();
    }

    @Test
    void shouldFailUpdateWithInvalidName() {
        // Arrange
        CategoryCreateDTO valid = new CategoryCreateDTO("Valid Name", "Valid description");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CategoryCreateDTO> request = new HttpEntity<>(valid, headers);

        // create category
        ResponseEntity<CategoryDTO> postResponse = restTemplate.postForEntity(getUrl(), request, CategoryDTO.class);
        UUID id = postResponse.getBody().getId();

        // create invalid category
        CategoryCreateDTO invalid = new CategoryCreateDTO("", "Still has description");
        HttpEntity<CategoryCreateDTO> badRequest = new HttpEntity<>(invalid, headers);

        // Act - PUT
        ResponseEntity<ProblemDetail> putResponse = restTemplate.exchange(
                getUrl() + "/" + id,
                HttpMethod.PUT,
                badRequest,
                ProblemDetail.class
        );

        // Assert
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(putResponse.getBody()).isNotNull();
        assertThat(putResponse.getBody().getProperties()).containsKey("errors");
    }


}
