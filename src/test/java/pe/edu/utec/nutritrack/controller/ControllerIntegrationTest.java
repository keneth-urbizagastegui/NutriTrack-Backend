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
import pe.edu.utec.nutritrack.dto.request.*;
import pe.edu.utec.nutritrack.dto.response.*;
import pe.edu.utec.nutritrack.model.ProductCategory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

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

    // ──────────────── Helper Methods ────────────────

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpResponse<String> post(String path, String body, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .header("Content-Type", "application/json")
                .GET();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String registerAndLogin(String username, String email, String password) throws Exception {
        // Register
        RegisterRequest reg = RegisterRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
        post("/api/v1/auth/register", objectMapper.writeValueAsString(reg), null);

        // Login
        LoginRequest login = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
        HttpResponse<String> loginRes = post("/api/v1/auth/login", objectMapper.writeValueAsString(login), null);
        LoginResponse loginDto = objectMapper.readValue(loginRes.body(), LoginResponse.class);
        return loginDto.getAccessToken();
    }

    // ──────────────── Tests ────────────────

    @Test
    void shouldRegisterAndLoginSuccessfully() throws Exception {
        // 1. Register a standard user
        RegisterRequest registerUser = RegisterRequest.builder()
                .username("regularUser")
                .email("user@nutritrack.com")
                .password("Password123!")
                .build();

        HttpResponse<String> regUserRes = post("/api/v1/auth/register", objectMapper.writeValueAsString(registerUser), null);
        assertThat(regUserRes.statusCode()).isEqualTo(201);
        RegisterResponse regUserDto = objectMapper.readValue(regUserRes.body(), RegisterResponse.class);
        assertThat(regUserDto.getUsername()).isEqualTo("regularUser");

        // 2. Register an admin user (username must contain "admin")
        RegisterRequest registerAdmin = RegisterRequest.builder()
                .username("adminUser")
                .email("admin@nutritrack.com")
                .password("Password123!")
                .build();

        HttpResponse<String> regAdminRes = post("/api/v1/auth/register", objectMapper.writeValueAsString(registerAdmin), null);
        assertThat(regAdminRes.statusCode()).isEqualTo(201);

        // 3. Login as standard user
        LoginRequest loginUser = LoginRequest.builder()
                .username("regularUser")
                .password("Password123!")
                .build();

        HttpResponse<String> logUserRes = post("/api/v1/auth/login", objectMapper.writeValueAsString(loginUser), null);
        assertThat(logUserRes.statusCode()).isEqualTo(200);
        LoginResponse logUserDto = objectMapper.readValue(logUserRes.body(), LoginResponse.class);
        String userToken = logUserDto.getAccessToken();
        assertThat(userToken).isNotBlank();

        // 4. Login as admin user
        LoginRequest loginAdmin = LoginRequest.builder()
                .username("adminUser")
                .password("Password123!")
                .build();

        HttpResponse<String> logAdminRes = post("/api/v1/auth/login", objectMapper.writeValueAsString(loginAdmin), null);
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

        HttpResponse<String> prodUserRes = post("/api/v1/products", objectMapper.writeValueAsString(productRequest), userToken);
        assertThat(prodUserRes.statusCode()).isEqualTo(403);

        // 6. Create product as admin (Should succeed - 201)
        HttpResponse<String> prodAdminRes = post("/api/v1/products", objectMapper.writeValueAsString(productRequest), adminToken);
        assertThat(prodAdminRes.statusCode()).isEqualTo(201);
        ProductResponse prodAdminDto = objectMapper.readValue(prodAdminRes.body(), ProductResponse.class);
        assertThat(prodAdminDto.getId()).isNotNull();
        assertThat(prodAdminDto.getName()).isEqualTo("Whey Protein ISO");

        // 7. Get all products (public access - should succeed - 200)
        HttpResponse<String> getProductsRes = get("/api/v1/products", null);
        assertThat(getProductsRes.statusCode()).isEqualTo(200);
        assertThat(getProductsRes.body()).contains("Whey Protein ISO");
    }

    @Test
    void shouldCrudSuppliersSuccessfully() throws Exception {
        // Setup: register admin & get token
        String adminToken = registerAndLogin("adminSupplier", "admin.sup@nutritrack.com", "Password123!");

        // 1. Create a supplier as admin (201)
        SupplierRequest supplierReq = SupplierRequest.builder()
                .name("Green Farms S.A.")
                .contactEmail("info@greenfarms.com")
                .isActive(true)
                .build();

        HttpResponse<String> createRes = post("/api/v1/suppliers", objectMapper.writeValueAsString(supplierReq), adminToken);
        assertThat(createRes.statusCode()).isEqualTo(201);
        SupplierResponse supplierDto = objectMapper.readValue(createRes.body(), SupplierResponse.class);
        assertThat(supplierDto.getName()).isEqualTo("Green Farms S.A.");
        assertThat(supplierDto.get_links()).isNotNull();
        Long supplierId = supplierDto.getId();

        // 2. Get all suppliers (public - 200)
        HttpResponse<String> getAllRes = get("/api/v1/suppliers", null);
        assertThat(getAllRes.statusCode()).isEqualTo(200);
        assertThat(getAllRes.body()).contains("Green Farms S.A.");

        // 3. Get supplier by ID (authenticated - 200)
        HttpResponse<String> getByIdRes = get("/api/v1/suppliers/" + supplierId, adminToken);
        assertThat(getByIdRes.statusCode()).isEqualTo(200);
        assertThat(getByIdRes.body()).contains("Green Farms S.A.");
    }

    @Test
    void shouldCrudIngredientsSuccessfully() throws Exception {
        // Setup: register admin & get token
        String adminToken = registerAndLogin("adminIngredient", "admin.ing@nutritrack.com", "Password123!");

        // 1. Create an ingredient as admin (201)
        IngredientRequest ingredientReq = IngredientRequest.builder()
                .name("Cacao en Polvo")
                .description("Cacao orgánico premium")
                .shelfLifeDays(365)
                .build();

        HttpResponse<String> createRes = post("/api/v1/ingredients", objectMapper.writeValueAsString(ingredientReq), adminToken);
        assertThat(createRes.statusCode()).isEqualTo(201);
        IngredientResponse ingredientDto = objectMapper.readValue(createRes.body(), IngredientResponse.class);
        assertThat(ingredientDto.getName()).isEqualTo("Cacao en Polvo");
        assertThat(ingredientDto.get_links()).isNotNull();
        Long ingredientId = ingredientDto.getId();

        // 2. Get all ingredients (public - 200)
        HttpResponse<String> getAllRes = get("/api/v1/ingredients", null);
        assertThat(getAllRes.statusCode()).isEqualTo(200);
        assertThat(getAllRes.body()).contains("Cacao en Polvo");

        // 3. Get ingredient by ID (authenticated - 200)
        HttpResponse<String> getByIdRes = get("/api/v1/ingredients/" + ingredientId, adminToken);
        assertThat(getByIdRes.statusCode()).isEqualTo(200);
        assertThat(getByIdRes.body()).contains("Cacao en Polvo");
    }

    @Test
    void shouldCreateBatchAndAssociateIngredient() throws Exception {
        // Setup: register admin & get token
        String adminToken = registerAndLogin("adminBatch", "admin.batch@nutritrack.com", "Password123!");

        // 1. Create a product
        ProductRequest productReq = ProductRequest.builder()
                .name("Granola Premium")
                .description("Mezcla artesanal de cereales")
                .brand("NutriMix")
                .category(ProductCategory.READY_MEAL)
                .proteinPer100g(10.0)
                .carbsPer100g(60.0)
                .fatPer100g(15.0)
                .build();

        HttpResponse<String> prodRes = post("/api/v1/products", objectMapper.writeValueAsString(productReq), adminToken);
        assertThat(prodRes.statusCode()).isEqualTo(201);
        ProductResponse prodDto = objectMapper.readValue(prodRes.body(), ProductResponse.class);
        Long productId = prodDto.getId();

        // 2. Create a batch under that product
        BatchRequest batchReq = BatchRequest.builder()
                .batchNumber("B-GRAN-001")
                .productionDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusMonths(6))
                .build();

        HttpResponse<String> batchRes = post("/api/v1/products/" + productId + "/batches", objectMapper.writeValueAsString(batchReq), adminToken);
        assertThat(batchRes.statusCode()).isEqualTo(201);
        BatchResponse batchDto = objectMapper.readValue(batchRes.body(), BatchResponse.class);
        Long batchId = batchDto.getId();
        assertThat(batchDto.get_links()).isNotNull();

        // 3. Create a supplier
        SupplierRequest supplierReq = SupplierRequest.builder()
                .name("Andean Harvest")
                .contactEmail("harvest@andean.com")
                .isActive(true)
                .build();

        HttpResponse<String> supRes = post("/api/v1/suppliers", objectMapper.writeValueAsString(supplierReq), adminToken);
        assertThat(supRes.statusCode()).isEqualTo(201);
        SupplierResponse supDto = objectMapper.readValue(supRes.body(), SupplierResponse.class);
        Long supplierId = supDto.getId();

        // 4. Create an ingredient
        IngredientRequest ingReq = IngredientRequest.builder()
                .name("Avena en Hojuelas")
                .description("Avena integral laminada")
                .shelfLifeDays(180)
                .build();

        HttpResponse<String> ingRes = post("/api/v1/ingredients", objectMapper.writeValueAsString(ingReq), adminToken);
        assertThat(ingRes.statusCode()).isEqualTo(201);
        IngredientResponse ingDto = objectMapper.readValue(ingRes.body(), IngredientResponse.class);
        Long ingredientId = ingDto.getId();

        // 5. Associate ingredient + supplier to batch
        String batchIngBody = String.format(
                "{\"ingredientId\":%d,\"supplierId\":%d,\"arrivalDate\":\"%s\"}",
                ingredientId, supplierId, LocalDate.now().toString());

        HttpResponse<String> assocRes = post("/api/v1/batches/" + batchId + "/ingredients", batchIngBody, adminToken);
        assertThat(assocRes.statusCode()).isEqualTo(201);
        assertThat(assocRes.body()).contains("Avena en Hojuelas");
        assertThat(assocRes.body()).contains("Andean Harvest");

        // 6. Verify traceability endpoint shows the ingredient (public)
        HttpResponse<String> traceRes = get("/api/v1/batches/" + batchId + "/traceability", null);
        assertThat(traceRes.statusCode()).isEqualTo(200);
        assertThat(traceRes.body()).contains("Avena en Hojuelas");
        assertThat(traceRes.body()).contains("Andean Harvest");
    }

    @Test
    void shouldManageUserAllergensAndConsumption() throws Exception {
        // Setup: register admin to create data, and a regular user to consume
        String adminToken = registerAndLogin("adminConsumption", "admin.cons@nutritrack.com", "Password123!");
        String userToken = registerAndLogin("userConsumer", "consumer@nutritrack.com", "Password123!");

        // 1. Admin creates a product
        ProductRequest productReq = ProductRequest.builder()
                .name("Energy Bar")
                .description("High protein energy bar")
                .brand("PowerFit")
                .category(ProductCategory.SUPPLEMENT)
                .proteinPer100g(25.0)
                .carbsPer100g(40.0)
                .fatPer100g(10.0)
                .build();

        HttpResponse<String> prodRes = post("/api/v1/products", objectMapper.writeValueAsString(productReq), adminToken);
        assertThat(prodRes.statusCode()).isEqualTo(201);
        ProductResponse prodDto = objectMapper.readValue(prodRes.body(), ProductResponse.class);

        // 2. Admin creates a batch
        BatchRequest batchReq = BatchRequest.builder()
                .batchNumber("B-ENERGY-100")
                .productionDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusMonths(3))
                .build();

        HttpResponse<String> batchRes = post("/api/v1/products/" + prodDto.getId() + "/batches", objectMapper.writeValueAsString(batchReq), adminToken);
        assertThat(batchRes.statusCode()).isEqualTo(201);
        BatchResponse batchDto = objectMapper.readValue(batchRes.body(), BatchResponse.class);

        // 3. User registers a consumption
        String consumptionBody = String.format(
                "{\"batchId\":%d,\"quantityGrams\":150,\"consumptionDate\":\"%s\"}",
                batchDto.getId(), java.time.LocalDateTime.now().toString());

        HttpResponse<String> consumeRes = post("/api/v1/consumption", consumptionBody, userToken);
        assertThat(consumeRes.statusCode()).isEqualTo(201);
        assertThat(consumeRes.body()).contains("consumedMacros");

        // 4. User checks consumption history
        HttpResponse<String> historyRes = get("/api/v1/consumption", userToken);
        assertThat(historyRes.statusCode()).isEqualTo(200);
        assertThat(historyRes.body()).contains("Energy Bar");

        // 5. Admin creates an ingredient for allergen test
        IngredientRequest peanutReq = IngredientRequest.builder()
                .name("Peanut Butter Extract")
                .description("Concentrated peanut extract")
                .shelfLifeDays(90)
                .build();

        HttpResponse<String> peanutRes = post("/api/v1/ingredients", objectMapper.writeValueAsString(peanutReq), adminToken);
        assertThat(peanutRes.statusCode()).isEqualTo(201);
        IngredientResponse peanutDto = objectMapper.readValue(peanutRes.body(), IngredientResponse.class);

        // 6. User marks peanut as allergen
        String allergenBody = String.format("{\"ingredientId\":%d}", peanutDto.getId());
        HttpResponse<String> allergenRes = post("/api/v1/users/allergens", allergenBody, userToken);
        assertThat(allergenRes.statusCode()).isEqualTo(200);
        assertThat(allergenRes.body()).contains("correctamente");
    }
}
