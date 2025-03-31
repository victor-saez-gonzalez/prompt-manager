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
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void cleanDatabase() {
        categoryRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveCategorySuccessfully() {

        // Arrange
        CategoryCreateDTO requestDto = new CategoryCreateDTO("Integration Test", "Test Description");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CategoryCreateDTO> request = new HttpEntity<>(requestDto, headers);

        // Act - POST
        ResponseEntity<CategoryDTO> postResponse = restTemplate.postForEntity(getUrl(""), request, CategoryDTO.class);

        // Assert - POST response
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    void shouldDeleteCategorySuccessfully() {
        // Arrange
        CategoryCreateDTO requestDto = new CategoryCreateDTO("To Be Deleted", "Temporary category");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CategoryCreateDTO> request = new HttpEntity<>(requestDto, headers);

        // Create a new category
        ResponseEntity<CategoryDTO> postResponse = restTemplate.postForEntity(getUrl(""), request, CategoryDTO.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UUID id = postResponse.getBody().getId();

        // Act - DELETE
        restTemplate.delete(getUrl("/" + id));

        // Assert - GET  must return 404
        ResponseEntity<CategoryDTO> getResponse = restTemplate.getForEntity(getUrl("/" + id), CategoryDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingCategory() {
        UUID fakeId = UUID.randomUUID();

        CategoryCreateDTO updateDto = new CategoryCreateDTO("Update", "Nonexistent ID");
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
        // create a category
        CategoryCreateDTO dto = new CategoryCreateDTO("Listable", "Demo");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(getUrl(""), new HttpEntity<>(dto, headers), CategoryDTO.class);

        // call to get all (paginated response)
        ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                getUrl(""),
                HttpMethod.GET,
                null,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // extract 'content' and map to CategoryDTO list
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
        assertThat(content).isNotNull();
        assertThat(content.size()).isGreaterThanOrEqualTo(1);


        List<CategoryDTO> categories = content.stream()
                .map(item -> objectMapper.convertValue(item, CategoryDTO.class))
                .collect(Collectors.toList());

        assertThat(categories).extracting("name").contains("Listable");
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
    void shouldReturnPagedCategoriesSortedByName() {
        // create 3 categories
        postCategory("Zeta");
        postCategory("Alpha");
        postCategory("Beta");

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
    void shouldEnforceMaxPageSizeLimit() {


        // Create 60 categories
        IntStream.rangeClosed(1, 60).forEach(i ->
                postCategory("Category " + i)
        );

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
    void shouldReturnSecondPageSortedDescending() {


        // Create 5 categories with known names
        postCategory("Alpha");
        postCategory("Bravo");
        postCategory("Charlie");
        postCategory("Delta");
        postCategory("Echo");

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




    private void postCategory(String name) {
        CategoryCreateDTO dto = new CategoryCreateDTO(name, name + " description");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(getUrl(""), new HttpEntity<>(dto, headers), CategoryDTO.class);
    }



}
