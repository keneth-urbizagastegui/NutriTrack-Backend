package pe.edu.utec.nutritrack.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utec.nutritrack.model.*;
import pe.edu.utec.nutritrack.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final BatchIngredientRepository batchIngredientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Iniciando revisión de siembra de datos NutriTrack...");

        // 1. Sembrar Ingredientes si está vacío
        if (ingredientRepository.count() == 0) {
            System.out.println("Sembrando ingredientes...");
            Ingredient whey = Ingredient.builder()
                    .name("Concentrado de Proteína de Suero")
                    .description("Materia prima rica en aminoácidos para el desarrollo muscular")
                    .shelfLifeDays(365)
                    .build();

            Ingredient creatineRaw = Ingredient.builder()
                    .name("Creatina Monohidratada Pura")
                    .description("Compuesto nitrogenado para la resíntesis de ATP")
                    .shelfLifeDays(730)
                    .build();

            Ingredient lactose = Ingredient.builder()
                    .name("Lactosa")
                    .description("Azúcar de la leche, alérgeno común")
                    .shelfLifeDays(365)
                    .build();

            Ingredient peanut = Ingredient.builder()
                    .name("Maní")
                    .description("Fruto seco, alérgeno común y fuente de grasas saludables")
                    .shelfLifeDays(180)
                    .build();

            Ingredient caffeine = Ingredient.builder()
                    .name("Cafeína Anhidra")
                    .description("Estimulante del sistema nervioso central")
                    .shelfLifeDays(540)
                    .build();

            Ingredient sucralose = Ingredient.builder()
                    .name("Sucralosa")
                    .description("Edulcorante artificial sin calorías")
                    .shelfLifeDays(730)
                    .build();

            Ingredient soyLecithin = Ingredient.builder()
                    .name("Lecitina de Soya")
                    .description("Emulsionante natural derivado de la soya, alérgeno común")
                    .shelfLifeDays(365)
                    .build();

            Ingredient maltodextrin = Ingredient.builder()
                    .name("Maltodextrina")
                    .description("Carbohidrato de rápida absorción")
                    .shelfLifeDays(540)
                    .build();

            Ingredient betaAlanine = Ingredient.builder()
                    .name("Beta-Alanina")
                    .description("Aminoácido que incrementa los niveles de carnosina muscular")
                    .shelfLifeDays(730)
                    .build();

            Ingredient cocoa = Ingredient.builder()
                    .name("Cacao en Polvo")
                    .description("Aportador de sabor y antioxidantes")
                    .shelfLifeDays(365)
                    .build();

            ingredientRepository.saveAll(List.of(whey, creatineRaw, lactose, peanut, caffeine, sucralose, soyLecithin, maltodextrin, betaAlanine, cocoa));
        }

        // 2. Sembrar Proveedores si está vacío
        if (supplierRepository.count() == 0) {
            System.out.println("Sembrando proveedores...");
            Supplier sup1 = Supplier.builder()
                    .name("Lácteos del Sur S.A.")
                    .contactEmail("contacto@lacteosdelsur.com")
                    .isActive(true)
                    .build();

            Supplier sup2 = Supplier.builder()
                    .name("Global Chemical Import")
                    .contactEmail("ventas@globalchem.com")
                    .isActive(true)
                    .build();

            Supplier sup3 = Supplier.builder()
                    .name("Distribuidora NutriFit")
                    .contactEmail("logistica@nutrifit.com")
                    .isActive(true)
                    .build();

            Supplier sup4 = Supplier.builder()
                    .name("Envases Lima S.A.")
                    .contactEmail("contacto@envaseslima.com")
                    .isActive(true)
                    .build();

            Supplier sup5 = Supplier.builder()
                    .name("Sabores del Mundo S.A.")
                    .contactEmail("sabores@saboresmundo.com")
                    .isActive(true)
                    .build();

            supplierRepository.saveAll(List.of(sup1, sup2, sup3, sup4, sup5));
        }

        // 3. Sembrar Productos si está vacío
        if (productRepository.count() == 0) {
            System.out.println("Sembrando productos...");
            Product prod1 = Product.builder()
                    .name("Ultra Whey Isolada")
                    .brand("NutriFit UTEC")
                    .description("Aislado de proteína de suero de leche de rápida absorción libre de lactosa")
                    .category(ProductCategory.SUPPLEMENT)
                    .proteinPer100g(85.0)
                    .carbsPer100g(2.5)
                    .fatPer100g(1.0)
                    .build();

            Product prod2 = Product.builder()
                    .name("Creatina Monohidratada Pure")
                    .brand("NutriFit UTEC")
                    .description("Creatina monohidratada micronizada de alta pureza")
                    .category(ProductCategory.SUPPLEMENT)
                    .proteinPer100g(0.0)
                    .carbsPer100g(0.0)
                    .fatPer100g(0.0)
                    .build();

            Product prod3 = Product.builder()
                    .name("Pre-Entrenamiento Explosivo")
                    .brand("NutriFit UTEC")
                    .description("Suplemento pre-entreno con cafeína y beta-alanina para máxima concentración")
                    .category(ProductCategory.BEVERAGE)
                    .proteinPer100g(5.0)
                    .carbsPer100g(12.0)
                    .fatPer100g(0.5)
                    .build();

            Product prod4 = Product.builder()
                    .name("Barra de Proteína con Maní")
                    .brand("NutriFit UTEC")
                    .description("Barra energética alta en proteína, maní tostado y cacao")
                    .category(ProductCategory.READY_MEAL)
                    .proteinPer100g(30.0)
                    .carbsPer100g(25.0)
                    .fatPer100g(12.0)
                    .build();

            Product prod5 = Product.builder()
                    .name("Proteína Vegana de Soya")
                    .brand("NutriFit UTEC")
                    .description("Proteína aislada de soya 100% de origen vegetal")
                    .category(ProductCategory.SUPPLEMENT)
                    .proteinPer100g(78.0)
                    .carbsPer100g(4.0)
                    .fatPer100g(1.5)
                    .build();

            productRepository.saveAll(List.of(prod1, prod2, prod3, prod4, prod5));
        }

        // 4. Sembrar Usuarios específicos si no existen
        User adminUser = userRepository.findByUsername("admin").orElse(null);
        if (adminUser == null) {
            System.out.println("Sembrando usuario admin...");
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(Role.ROLE_USER);
            adminRoles.add(Role.ROLE_ADMIN);
            adminRoles.add(Role.ROLE_MANAGER);

            adminUser = User.builder()
                    .username("admin")
                    .email("admin@utec.edu.pe")
                    .password(passwordEncoder.encode("StrongPassword123!"))
                    .createdAt(LocalDateTime.now())
                    .roles(adminRoles)
                    .build();
            userRepository.save(adminUser);
        } else {
            System.out.println("Actualizando credenciales y roles de usuario admin a StrongPassword123!...");
            adminUser.setPassword(passwordEncoder.encode("StrongPassword123!"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(Role.ROLE_USER);
            adminRoles.add(Role.ROLE_ADMIN);
            adminRoles.add(Role.ROLE_MANAGER);
            adminUser.setRoles(adminRoles);
            userRepository.save(adminUser);
        }

        User managerUser = userRepository.findByUsername("manager").orElse(null);
        if (managerUser == null) {
            System.out.println("Sembrando usuario manager...");
            Set<Role> managerRoles = new HashSet<>();
            managerRoles.add(Role.ROLE_USER);
            managerRoles.add(Role.ROLE_MANAGER);

            managerUser = User.builder()
                    .username("manager")
                    .email("manager@utec.edu.pe")
                    .password(passwordEncoder.encode("StrongPassword123!"))
                    .createdAt(LocalDateTime.now())
                    .roles(managerRoles)
                    .build();
            userRepository.save(managerUser);
        } else {
            System.out.println("Actualizando credenciales y roles de usuario manager a StrongPassword123!...");
            managerUser.setPassword(passwordEncoder.encode("StrongPassword123!"));
            Set<Role> managerRoles = new HashSet<>();
            managerRoles.add(Role.ROLE_USER);
            managerRoles.add(Role.ROLE_MANAGER);
            managerUser.setRoles(managerRoles);
            userRepository.save(managerUser);
        }

        User regularUser = userRepository.findByUsername("victor.fitness").orElse(null);
        if (regularUser == null) {
            System.out.println("Sembrando usuario victor.fitness...");
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(Role.ROLE_USER);

            regularUser = User.builder()
                    .username("victor.fitness")
                    .email("victor@utec.edu.pe")
                    .password(passwordEncoder.encode("StrongPassword123!"))
                    .createdAt(LocalDateTime.now())
                    .roles(userRoles)
                    .build();
            userRepository.save(regularUser);
        } else {
            System.out.println("Actualizando credenciales y roles de usuario victor.fitness a StrongPassword123!...");
            regularUser.setPassword(passwordEncoder.encode("StrongPassword123!"));
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(Role.ROLE_USER);
            regularUser.setRoles(userRoles);
            userRepository.save(regularUser);
        }

        // Sembrar alérgenos preestablecidos para victor.fitness
        regularUser = userRepository.findByUsername("victor.fitness").orElse(null);
        if (regularUser != null && regularUser.getAllergens().isEmpty()) {
            System.out.println("Sembrando alérgenos para victor.fitness...");
            Ingredient lactose = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Lactosa"))
                    .findFirst().orElse(null);
            Ingredient peanut = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Maní"))
                    .findFirst().orElse(null);
            if (lactose != null) regularUser.getAllergens().add(lactose);
            if (peanut != null) regularUser.getAllergens().add(peanut);
            userRepository.save(regularUser);
        }

        // 5. Sembrar Lotes y Trazabilidades si está vacío
        if (batchRepository.count() == 0) {
            System.out.println("Sembrando lotes y trazabilidades de prueba...");
            
            // Obtener productos y datos base creados
            Product prod1 = productRepository.findAll().stream()
                    .filter(p -> p.getName().contains("Ultra Whey"))
                    .findFirst().orElse(null);

            Product prod2 = productRepository.findAll().stream()
                    .filter(p -> p.getName().contains("Creatina"))
                    .findFirst().orElse(null);

            Product prod3 = productRepository.findAll().stream()
                    .filter(p -> p.getName().contains("Pre-Entrenamiento"))
                    .findFirst().orElse(null);

            Product prod4 = productRepository.findAll().stream()
                    .filter(p -> p.getName().contains("Barra"))
                    .findFirst().orElse(null);

            Product prod5 = productRepository.findAll().stream()
                    .filter(p -> p.getName().contains("Vegana"))
                    .findFirst().orElse(null);

            Ingredient whey = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Proteína de Suero"))
                    .findFirst().orElse(null);

            Ingredient lactose = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Lactosa"))
                    .findFirst().orElse(null);

            Ingredient creatineRaw = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Creatina"))
                    .findFirst().orElse(null);

            Ingredient peanut = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Maní"))
                    .findFirst().orElse(null);

            Ingredient caffeine = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Cafeína"))
                    .findFirst().orElse(null);

            Ingredient betaAlanine = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Beta-Alanina"))
                    .findFirst().orElse(null);

            Ingredient cocoa = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Cacao"))
                    .findFirst().orElse(null);

            Ingredient soyLecithin = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Lecitina"))
                    .findFirst().orElse(null);

            Ingredient sucralose = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Sucralosa"))
                    .findFirst().orElse(null);

            Supplier sup1 = supplierRepository.findAll().stream()
                    .filter(s -> s.getName().contains("Lácteos"))
                    .findFirst().orElse(null);

            Supplier sup2 = supplierRepository.findAll().stream()
                    .filter(s -> s.getName().contains("Global"))
                    .findFirst().orElse(null);

            Supplier sup3 = supplierRepository.findAll().stream()
                    .filter(s -> s.getName().contains("NutriFit"))
                    .findFirst().orElse(null);

            Supplier sup5 = supplierRepository.findAll().stream()
                    .filter(s -> s.getName().contains("Sabores"))
                    .findFirst().orElse(null);

            if (prod1 != null && prod2 != null && prod3 != null && prod4 != null && prod5 != null) {
                // Lote 1: Ultra Whey Isolada (ACTIVE)
                Batch batch1 = Batch.builder()
                        .batchNumber("W-ISO-099")
                        .productionDate(LocalDate.now().minusDays(10))
                        .expirationDate(LocalDate.now().plusYears(1))
                        .status(BatchStatus.ACTIVE)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/W-ISO-099.png")
                        .product(prod1)
                        .build();

                // Lote 2: Creatina Monohidratada Pure (ACTIVE)
                Batch batch2 = Batch.builder()
                        .batchNumber("CRE-202")
                        .productionDate(LocalDate.now().minusDays(15))
                        .expirationDate(LocalDate.now().plusYears(2))
                        .status(BatchStatus.ACTIVE)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/CRE-202.png")
                        .product(prod2)
                        .build();

                // Lote 3: Pre-Entrenamiento Explosivo (ACTIVE)
                Batch batch3 = Batch.builder()
                        .batchNumber("PRE-303")
                        .productionDate(LocalDate.now().minusDays(5))
                        .expirationDate(LocalDate.now().plusYears(1))
                        .status(BatchStatus.ACTIVE)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/PRE-303.png")
                        .product(prod3)
                        .build();

                // Lote 4: Barra de Proteína con Maní (ACTIVE, contiene alérgenos comunes)
                Batch batch4 = Batch.builder()
                        .batchNumber("PEANUT-BAR-404")
                        .productionDate(LocalDate.now().minusDays(8))
                        .expirationDate(LocalDate.now().plusMonths(6))
                        .status(BatchStatus.ACTIVE)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/PEANUT-BAR-404.png")
                        .product(prod4)
                        .build();

                // Lote 5: Proteína Vegana de Soya (RECALLED, retirado del mercado)
                Batch batch5 = Batch.builder()
                        .batchNumber("RECALL-BAD-505")
                        .productionDate(LocalDate.now().minusDays(30))
                        .expirationDate(LocalDate.now().plusYears(1))
                        .status(BatchStatus.RECALLED)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/RECALL-BAD-505.png")
                        .product(prod5)
                        .build();

                batchRepository.saveAll(List.of(batch1, batch2, batch3, batch4, batch5));

                // Guardar asociaciones
                // Lote 1
                BatchIngredient bi1 = BatchIngredient.builder()
                        .batch(batch1)
                        .ingredient(whey)
                        .supplier(sup1)
                        .arrivalDate(LocalDate.now().minusDays(12))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                BatchIngredient bi2 = BatchIngredient.builder()
                        .batch(batch1)
                        .ingredient(lactose)
                        .supplier(sup1)
                        .arrivalDate(LocalDate.now().minusDays(12))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                // Lote 2
                BatchIngredient bi3 = BatchIngredient.builder()
                        .batch(batch2)
                        .ingredient(creatineRaw)
                        .supplier(sup2)
                        .arrivalDate(LocalDate.now().minusDays(20))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                // Lote 3
                BatchIngredient bi4 = BatchIngredient.builder()
                        .batch(batch3)
                        .ingredient(caffeine)
                        .supplier(sup2)
                        .arrivalDate(LocalDate.now().minusDays(6))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                BatchIngredient bi5 = BatchIngredient.builder()
                        .batch(batch3)
                        .ingredient(betaAlanine)
                        .supplier(sup3)
                        .arrivalDate(LocalDate.now().minusDays(6))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                // Lote 4
                BatchIngredient bi6 = BatchIngredient.builder()
                        .batch(batch4)
                        .ingredient(peanut)
                        .supplier(sup3)
                        .arrivalDate(LocalDate.now().minusDays(9))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                BatchIngredient bi7 = BatchIngredient.builder()
                        .batch(batch4)
                        .ingredient(cocoa)
                        .supplier(sup5)
                        .arrivalDate(LocalDate.now().minusDays(9))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                // Lote 5
                BatchIngredient bi8 = BatchIngredient.builder()
                        .batch(batch5)
                        .ingredient(soyLecithin)
                        .supplier(sup2)
                        .arrivalDate(LocalDate.now().minusDays(35))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                BatchIngredient bi9 = BatchIngredient.builder()
                        .batch(batch5)
                        .ingredient(sucralose)
                        .supplier(sup3)
                        .arrivalDate(LocalDate.now().minusDays(35))
                        .freshnessStatus(FreshnessStatus.FRESH)
                        .build();

                batchIngredientRepository.saveAll(List.of(bi1, bi2, bi3, bi4, bi5, bi6, bi7, bi8, bi9));
            }
        }

        System.out.println("Revisión y siembra de base de datos finalizada.");
    }
}
