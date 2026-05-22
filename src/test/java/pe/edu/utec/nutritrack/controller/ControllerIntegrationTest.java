package pe.edu.utec.nutritrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pe.edu.utec.nutritrack.dto.request.LoginRequest;
import pe.edu.utec.nutritrack.dto.request.RegisterRequest;
import pe.edu.utec.nutritrack.dto.request.ProductRequest;
import pe.edu.utec.nutritrack.dto.response.LoginResponse;
import pe.edu.utec.nutritrack.dto.response.RegisterResponse;
import pe.edu.utec.nutritrack.dto.response.ProductResponse;
import pe.edu.utec.nutritrack.model.ProductCategory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class ControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("nutritrack_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @LocalServerPort
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void shouldRegisterAndLoginSuccessfully() throws Exception {
        String baseUrl = "http://localhost:" + port;

        // 1. Register a standard user
        RegisterRequest registerUser = RegisterRequest.builder()
                .username("regularUser")
                .email("user@nutritrack.com")
                .password("Password123!")
                .build();

        HttpRequest regUserReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(registerUser)))
                .build();

        HttpResponse<String> regUserRes = httpClient.send(regUserReq, HttpResponse.BodyHandlers.ofString());
        assertThat(regUserRes.statusCode()).isEqualTo(201);
        RegisterResponse regUserDto = objectMapper.readValue(regUserRes.body(), RegisterResponse.class);
        assertThat(regUserDto.getUsername()).isEqualTo("regularUser");

        // 2. Register an admin user (username must contain "admin")
        RegisterRequest registerAdmin = RegisterRequest.builder()
                .username("adminUser")
                .email("admin@nutritrack.com")
                .password("Password123!")
                .build();

        HttpRequest regAdminReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(registerAdmin)))
                .build();

        HttpResponse<String> regAdminRes = httpClient.send(regAdminReq, HttpResponse.BodyHandlers.ofString());
        assertThat(regAdminRes.statusCode()).isEqualTo(201);

        // 3. Login as standard user
        LoginRequest loginUser = LoginRequest.builder()
                .username("regularUser")
                .password("Password123!")
                .build();

        HttpRequest logUserReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(loginUser)))
                .build();

        HttpResponse<String> logUserRes = httpClient.send(logUserReq, HttpResponse.BodyHandlers.ofString());
        assertThat(logUserRes.statusCode()).isEqualTo(200);
        LoginResponse logUserDto = objectMapper.readValue(logUserRes.body(), LoginResponse.class);
        String userToken = logUserDto.getAccessToken();
        assertThat(userToken).isNotBlank();

        // 4. Login as admin user
        LoginRequest loginAdmin = LoginRequest.builder()
                .username("adminUser")
                .password("Password123!")
                .build();

        HttpRequest logAdminReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(loginAdmin)))
                .build();

        HttpResponse<String> logAdminRes = httpClient.send(logAdminReq, HttpResponse.BodyHandlers.ofString());
        assertThat(logAdminRes.statusCode()).isEqualTo(200);
        LoginResponse logAdminDto = objectMapper.readValue(logAdminRes.body(), LoginResponse.class);
        String adminToken = logAdminDto.getAccessToken();
        assertThat(adminToken).isNotBlank();

        // 5. Attempt to create a product as a standard user (Should be forbidden - 403)
        ProductRequest productRequest = ProductRequest.builder()
                .name("Whey Protein ISO")
                .description("High quality whey protein isolate")
                .brand("NutriBrand")
                .category(ProductCategory.SUPPLEMENT)
                .proteinPer100g(90.0)
                .carbsPer100g(1.0)
                .fatPer100g(0.5)
                .build();

        HttpRequest prodUserReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/products"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + userToken)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(productRequest)))
                .build();

        HttpResponse<String> prodUserRes = httpClient.send(prodUserReq, HttpResponse.BodyHandlers.ofString());
        assertThat(prodUserRes.statusCode()).isEqualTo(403);

        // 6. Create product as admin (Should succeed - 201)
        HttpRequest prodAdminReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/products"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(productRequest)))
                .build();

        HttpResponse<String> prodAdminRes = httpClient.send(prodAdminReq, HttpResponse.BodyHandlers.ofString());
        assertThat(prodAdminRes.statusCode()).isEqualTo(201);
        ProductResponse prodAdminDto = objectMapper.readValue(prodAdminRes.body(), ProductResponse.class);
        assertThat(prodAdminDto.getId()).isNotNull();
        assertThat(prodAdminDto.getName()).isEqualTo("Whey Protein ISO");

        // 7. Get all products (public access - should succeed - 200)
        HttpRequest getProductsReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/products"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> getProductsRes = httpClient.send(getProductsReq, HttpResponse.BodyHandlers.ofString());
        assertThat(getProductsRes.statusCode()).isEqualTo(200);
        assertThat(getProductsRes.body()).contains("Whey Protein ISO");
    }
}
