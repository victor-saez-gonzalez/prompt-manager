package com.vs.prompt.manager.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vs.prompt.manager.common.dto.CategoryCreateDTO;
import com.vs.prompt.manager.common.dto.CategoryDTO;
import com.vs.prompt.manager.persistence.repository.CategoryRepository;
import com.vs.prompt.manager.web.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class CategoryControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    private String getUrl(String path) {
        return "http://localhost:" + port + "/api/categories" + path;
    }

    @Test
    void shouldCreateAndRetrieveCategorySuccessfully() {
        // Arrange
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        CategoryCreateDTO requestDto = new CategoryCreateDTO("Integration Test", "Test Description", userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CategoryCreateDTO> request = new HttpEntity<>(requestDto, headers);

        // Act - POST
        ResponseEntity<CategoryDTO> postResponse = restTemplate.postForEntity(getUrl(""), request, CategoryDTO.class);

        // Assert - POST response
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CategoryDTO created = postResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Integration Test");
        assertThat(created.getDescription()).isEqualTo("Test Description");

        // Act - GET
        ResponseEntity<CategoryDTO> getResponse = restTemplate.getForEntity(getUrl("/" + created.getId()), CategoryDTO.class);

        // Assert - GET response
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CategoryDTO fetched = getResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(created.getId());
        assertThat(fetched.getName()).isEqualTo("Integration Test");
        assertThat(fetched.getDescription()).isEqualTo("Test Description");
    }


    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingCategory() {
        UUID fakeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID(); // Simulated userId

        CategoryCreateDTO updateDto = new CategoryCreateDTO("Update", "Nonexistent ID", userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CategoryCreateDTO> request = new HttpEntity<>(updateDto, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                getUrl("/" + fakeId),
                HttpMethod.PUT,
                request,
                ProblemDetail.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Resource Not Found");
    }

    @Test
    void shouldReturnCategoriesList() {
        // Call to get all categories (paginated)
        ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getUrl(""),
                HttpMethod.GET,
                null,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Extract 'content' from response
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
        assertThat(content).isNotNull();
        assertThat(content.size()).isGreaterThanOrEqualTo(10); // we have 10 categories in data.sql

        // Optional: verify that known category names exist
        List<CategoryDTO> categories = content.stream()
                .map(item -> objectMapper.convertValue(item, CategoryDTO.class))
                .toList();

        assertThat(categories).extracting("name")
                .contains("Utilities", "Education", "Entertainment");
    }



    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingCategory() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                getUrl("/" + nonExistentId),
                HttpMethod.DELETE,
                request,
                ProblemDetail.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetail problem = response.getBody();
        assertThat(problem).isNotNull();
        assertThat(problem.getTitle()).isEqualTo("Resource Not Found");
        assertThat(problem.getDetail()).contains("Category not found");
    }

    @Test
    @Sql(statements = {
            "DELETE FROM category",
            "DELETE FROM users",
            "INSERT INTO users (id, email, name, password, provider, provider_id) VALUES " +
                    "('11111111-1111-1111-1111-111111111111', 'john.doe@example.com', 'John Doe', 'hashedpassword', 'LOCAL', 'localid')"
    })
    void shouldReturnPagedCategoriesSortedByName() {


        UUID validUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // Create 60 categories for the valid user
        postCategory("Zeta", validUserId);
        postCategory("Alpha", validUserId);
        postCategory("Beta", validUserId);

        //  makes GET with pagination and ascendant order by name
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getUrl("?page=0&size=2&sort=name,asc"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();

        List<Map<String, Object>> content = (List<Map<String, Object>>) body.get("content");
        assertThat(content).hasSize(2);
        assertThat(content.get(0).get("name")).isEqualTo("Alpha");
        assertThat(content.get(1).get("name")).isEqualTo("Beta");

        // check metadata
        assertThat(body.get("totalElements")).isEqualTo(3);
        assertThat(body.get("totalPages")).isEqualTo(2);
        assertThat(body.get("size")).isEqualTo(2);
    }


    @Test
    @Sql(statements = {
            "DELETE FROM category",
            "DELETE FROM users",
            "INSERT INTO users (id, email, name, password, provider, provider_id) VALUES " +
                    "('11111111-1111-1111-1111-111111111111', 'john.doe@example.com', 'John Doe', 'hashedpassword', 'LOCAL', 'localid')"
    })
    void shouldEnforceMaxPageSizeLimit() {
        UUID validUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // Create 60 categories for the valid user
        IntStream.rangeClosed(1, 60).forEach(i -> postCategory("Category " + i, validUserId));

        // Request with size greater than MAX_PAGE_SIZE (50)
        int requestedSize = 100;
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getUrl("?page=0&size=" + requestedSize),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Validate response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();

        List<Map<String, Object>> content = (List<Map<String, Object>>) body.get("content");

        // Ensure result is capped at 50 (MAX_PAGE_SIZE)
        assertThat(content).hasSize(50);

        // Validate pagination metadata
        assertThat(body.get("totalElements")).isEqualTo(60);
        assertThat(body.get("totalPages")).isEqualTo(2); // 60 / 50 = 2 pages
    }

    @Test
    @Sql(statements = {
            "DELETE FROM category",
            "DELETE FROM users",
            "INSERT INTO users (id, email, name, password, provider, provider_id) VALUES " +
                    "('11111111-1111-1111-1111-111111111111', 'john.doe@example.com', 'John Doe', 'hashedpassword', 'LOCAL', 'localid')"
    })
    void shouldReturnSecondPageSortedDescending() {

        UUID validUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        postCategory("Alpha", validUserId);
        postCategory("Bravo", validUserId);
        postCategory("Charlie", validUserId);
        postCategory("Delta", validUserId);
        postCategory("Echo", validUserId);

        // Request page 1 with size 2, sorted descending by name
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getUrl("?page=1&size=2&sort=name,desc"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Validate response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();

        List<Map<String, Object>> content = (List<Map<String, Object>>) body.get("content");

        // On page 1 (second page), we should get the third and fourth elements in descending order
        // Expected order: Echo, Delta, Charlie, Bravo, Alpha
        // Page 0 => Echo, Delta
        // Page 1 => Charlie, Bravo
        assertThat(content).hasSize(2);
        assertThat(content.get(0).get("name")).isEqualTo("Charlie");
        assertThat(content.get(1).get("name")).isEqualTo("Bravo");

        // Validate metadata
        assertThat(body.get("number")).isEqualTo(1); // current page
        assertThat(body.get("size")).isEqualTo(2);   // requested size
        assertThat(body.get("totalElements")).isEqualTo(5);
        assertThat(body.get("totalPages")).isEqualTo(3);
        assertThat(body.get("first")).isEqualTo(false);
        assertThat(body.get("last")).isEqualTo(false);
    }

    private void postCategory(String name, UUID userId) {
        CategoryCreateDTO dto = new CategoryCreateDTO(name, name + " description", userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<CategoryDTO> response = restTemplate.postForEntity(
                getUrl(""),
                new HttpEntity<>(dto, headers),
                CategoryDTO.class
        );

        // Ensure category is created successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

}
