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
        System.out.println("Iniciando revisión de siembra de datos NutriTrack Perú...");

        // 1. Sembrar Proveedores Homologados si está vacío
        if (supplierRepository.count() == 0) {
            System.out.println("Sembrando proveedores peruanos...");
            Supplier sup1 = Supplier.builder().name("Montana S.A.").contactEmail("contacto@montana.com.pe").isActive(true).build();
            Supplier sup2 = Supplier.builder().name("Ecoandino S.A.C.").contactEmail("ventas@ecoandino.com.pe").isActive(true).build();
            Supplier sup3 = Supplier.builder().name("Química Suiza Industrial (QSI)").contactEmail("contacto@qsi.com.pe").isActive(true).build();
            Supplier sup4 = Supplier.builder().name("Frutarom Perú S.A. (IFF)").contactEmail("ventas@frutarom.com.pe").isActive(true).build();
            Supplier sup5 = Supplier.builder().name("Alicorp Soluciones").contactEmail("logistica@alicorp.com.pe").isActive(true).build();
            Supplier sup6 = Supplier.builder().name("Lácteos del Sur S.A. (Gloria)").contactEmail("contacto@gloria.com.pe").isActive(true).build();
            Supplier sup7 = Supplier.builder().name("Molitalia S.A.").contactEmail("ventas@molitalia.com.pe").isActive(true).build();
            Supplier sup8 = Supplier.builder().name("Aicacolor S.A.C.").contactEmail("info@aicacolor.com.pe").isActive(true).build();
            Supplier sup9 = Supplier.builder().name("Inversiones Argos Natural S.A.C.").contactEmail("ventas@argosnatural.com").isActive(true).build();
            Supplier sup10 = Supplier.builder().name("Procesadora Industrial Río Seco S.A.C.").contactEmail("contacto@rioseco.com.pe").isActive(true).build();

            supplierRepository.saveAll(List.of(sup1, sup2, sup3, sup4, sup5, sup6, sup7, sup8, sup9, sup10));
        }

        // 2. Sembrar Ingredientes si está vacío
        if (ingredientRepository.count() == 0) {
            System.out.println("Sembrando ingredientes peruanos...");
            Ingredient wpc = Ingredient.builder().name("Concentrado de Proteína de Suero de Leche (WPC 80%)").description("Base proteica estructural de absorción media").shelfLifeDays(365).build();
            Ingredient wpi = Ingredient.builder().name("Aislado de Proteína de Suero de Leche (WPI 90%)").description("Base de suero deslactosada y ultrafiltrada").shelfLifeDays(365).build();
            Ingredient creatine = Ingredient.builder().name("Monohidrato de Creatina Micronizada").description("Incremento de la resíntesis de ATP muscular").shelfLifeDays(730).build();
            Ingredient glutamine = Ingredient.builder().name("L-Glutamina Pura").description("Anticatabólico y soporte de barrera mucosa").shelfLifeDays(730).build();
            Ingredient caffeine = Ingredient.builder().name("Cafeína Anhidra USP").description("Potenciador del rendimiento y del estado de alerta").shelfLifeDays(540).build();
            Ingredient betaAlanine = Ingredient.builder().name("Beta-Alanina").description("Incrementador de carnosina y amortiguador celular").shelfLifeDays(730).build();
            Ingredient soyIsolate = Ingredient.builder().name("Aislado de Proteína de Soya").description("Proteína vegetal purificada, alérgeno de soya").shelfLifeDays(365).build();
            Ingredient oatFlour = Ingredient.builder().name("Harina de Avena Integral").description("Fuente de carbohidratos complejos, contiene gluten").shelfLifeDays(365).build();
            Ingredient macaAmarilla = Ingredient.builder().name("Maca Amarilla Orgánica Gelatinizada en Polvo").description("Adaptógeno andino rico en fitonutrientes").shelfLifeDays(540).build();
            Ingredient cocoa = Ingredient.builder().name("Polvo de Cacao Orgánico Fino de Aroma (VRAEM)").description("Flavonoides antioxidantes y aporte de sabor").shelfLifeDays(365).build();
            Ingredient quinua = Ingredient.builder().name("Quinua Orgánica Extruida / Molida").description("Cereal andino de alto valor nutritivo").shelfLifeDays(365).build();
            Ingredient kiwicha = Ingredient.builder().name("Kiwicha Orgánica Pop / Molida").description("Grano andino con alto contenido de lisina").shelfLifeDays(365).build();
            Ingredient espirulina = Ingredient.builder().name("Alga Espirulina Deshidratada en Polvo").description("Antioxidante natural y aporte proteico celular").shelfLifeDays(730).build();
            Ingredient sangrecita = Ingredient.builder().name("Sangrecita de Bovino Deshidratada en Polvo (Hemoglobina)").description("Hierro hemínico altamente absorbible contra la anemia").shelfLifeDays(365).build();
            Ingredient salMaras = Ingredient.builder().name("Sal de Maras Fina (Cloruro de Sodio)").description("Aporte de sodio y minerales traza sin refinar").shelfLifeDays(1000).build();
            Ingredient sucralose = Ingredient.builder().name("Sucralosa FCC").description("Edulcorante artificial sintético no calórico").shelfLifeDays(730).build();
            Ingredient vanillaFlavor = Ingredient.builder().name("Saborizante Artificial Vainilla Cream").description("Enmascarador de sabores amargos lácteos").shelfLifeDays(540).build();
            Ingredient chocolateFlavor = Ingredient.builder().name("Saborizante Artificial Chocolate Fudge").description("Saborizante de alta densidad organoléptica").shelfLifeDays(540).build();
            Ingredient almondPaste = Ingredient.builder().name("Pasta de Almendras").description("Grase monoinsaturada aglutinante, alérgeno de frutos secos").shelfLifeDays(180).build();
            Ingredient peanutPaste = Ingredient.builder().name("Pasta de Maní Tostado").description("Aporte de lípidos funcionales, alérgeno de maní").shelfLifeDays(180).build();
            Ingredient aguaymanto = Ingredient.builder().name("Aguaymanto Deshidratado Picado").description("Fruta seca ácida, contiene sulfitos").shelfLifeDays(180).build();
            Ingredient soyLecithin = Ingredient.builder().name("Lecitina de Soya USP").description("Emulsionante natural derivado de la soya").shelfLifeDays(365).build();
            Ingredient wheatFlour = Ingredient.builder().name("Harina de Trigo Fortificada").description("Soporte estructural de masa horneada, contiene gluten").shelfLifeDays(180).build();

            ingredientRepository.saveAll(List.of(
                    wpc, wpi, creatine, glutamine, caffeine, betaAlanine, soyIsolate, oatFlour,
                    macaAmarilla, cocoa, quinua, kiwicha, espirulina, sangrecita, salMaras,
                    sucralose, vanillaFlavor, chocolateFlavor, almondPaste, peanutPaste, aguaymanto,
                    soyLecithin, wheatFlour
            ));
        }

        // 3. Sembrar Productos si está vacío
        if (productRepository.count() == 0) {
            System.out.println("Sembrando catálogo de productos peruanos...");
            Product prod1 = Product.builder().name("Whey Pro (Sabor Vainilla)").brand("Universe Nutrition").description("Proteína de suero de alta calidad con L-Glutamina y vitaminas, ideal para post-entrenamiento.").category(ProductCategory.SUPPLEMENT).proteinPer100g(75.0).carbsPer100g(10.0).fatPer100g(4.0).build();
            Product prod2 = Product.builder().name("BigM (Sabor Chocolate)").brand("Universe Nutrition").description("Ganador de masa muscular avanzado con avena, proteínas de suero y soya.").category(ProductCategory.SUPPLEMENT).proteinPer100g(25.0).carbsPer100g(60.0).fatPer100g(5.0).build();
            Product prod3 = Product.builder().name("Iso Whey 90 (Sabor Fresa)").brand("Universe Nutrition").description("Aislado de proteína de suero al 90%, cero grasas y carbohidratos, rápida absorción.").category(ProductCategory.SUPPLEMENT).proteinPer100g(90.0).carbsPer100g(0.5).fatPer100g(0.2).build();
            Product prod4 = Product.builder().name("Premium 100% Whey (Vainilla)").brand("Lab Nutrition").description("Suplemento hiperproteico con mezcla de concentrado y aislado de suero lácteo.").category(ProductCategory.SUPPLEMENT).proteinPer100g(78.0).carbsPer100g(8.0).fatPer100g(3.5).build();
            Product prod5 = Product.builder().name("Pump Kong Pre-Entrenamiento").brand("Lab Nutrition").description("Fórmula de pre-entrenamiento explosiva con cafeína, beta-alanina y citrulina.").category(ProductCategory.SUPPLEMENT).proteinPer100g(5.0).carbsPer100g(20.0).fatPer100g(0.0).build();
            Product prod9 = Product.builder().name("Creatina Monohidratada Micronizada Pura").brand("Universe Nutrition").description("Creatina micronizada pura al 100% para el incremento de la fuerza, potencia muscular y volumen.").category(ProductCategory.SUPPLEMENT).proteinPer100g(0.0).carbsPer100g(0.0).fatPer100g(0.0).build();
            Product prod19 = Product.builder().name("Maca Amarilla Orgánica Gelatinizada").brand("Ecoandino").description("Suplemento energético andino ancestral que regula el sistema endocrino y mejora la resistencia.").category(ProductCategory.SUPPLEMENT).proteinPer100g(12.0).carbsPer100g(65.0).fatPer100g(1.2).build();
            Product prod20 = Product.builder().name("Cacao Orgánico Fino de Aroma").brand("Ecoandino").description("Superalimento rico en polifenoles, antioxidantes naturales y teobromina para energía limpia.").category(ProductCategory.SUPPLEMENT).proteinPer100g(19.5).carbsPer100g(45.0).fatPer100g(12.0).build();

            Product prod21 = Product.builder().name("Sporade Tropical").brand("AJE").description("Bebida isotónica rehidratante con electrolitos y sabor tropical, de rápida absorción.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(4.88).fatPer100g(0.0).build();
            Product prod24 = Product.builder().name("Gatorade Tropical").brand("PepsiCo").description("Bebida deportiva científicamente formulada con carbohidratos simples y electrolitos clave.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(6.0).fatPer100g(0.0).build();
            Product prod34 = Product.builder().name("Volt Maca (Energizante)").brand("AJE").description("Bebida energizante que fusiona taurina y cafeína con extracto natural de Maca andina.").category(ProductCategory.BEVERAGE).proteinPer100g(0.2).carbsPer100g(11.0).fatPer100g(0.0).build();
            Product prod40 = Product.builder().name("Chicha Morada Antiox Sport").brand("Andean Form").description("Bebida hidratante rica en antioxidantes provenientes del maíz morado, con limón y sal de Maras.").category(ProductCategory.BEVERAGE).proteinPer100g(0.1).carbsPer100g(6.2).fatPer100g(0.0).build();

            Product prod42 = Product.builder().name("Wild Protein Chocolate Maní").brand("Wild Foods").description("Barra proteica crujiente con pasta de maní, cacao fino de aroma y proteína de soya.").category(ProductCategory.READY_MEAL).proteinPer100g(33.0).carbsPer100g(22.0).fatPer100g(10.0).build();
            Product prod47 = Product.builder().name("Barra de Quinua y Maní").brand("Mamalama").description("Barra de cereales andinos naturales elaborada con quinua pop, maní tostado y endulzada de forma natural.").category(ProductCategory.READY_MEAL).proteinPer100g(12.0).carbsPer100g(52.0).fatPer100g(14.0).build();
            Product prod53 = Product.builder().name("Galleta Anti-Anémica y Proteica Clásica").brand("Nutri H").description("Famosa galleta peruana contra la anemia, enriquecida con sangre bovina (hierro hemínico), quinua y cacao.").category(ProductCategory.READY_MEAL).proteinPer100g(14.5).carbsPer100g(63.3).fatPer100g(12.5).build();
            Product prod60 = Product.builder().name("Snacks de Aguaymanto Deshidratado Orgánico").brand("Ecoandino").description("Aguaymanto (goldenberries) deshidratado 100% orgánico y certificado.").category(ProductCategory.READY_MEAL).proteinPer100g(3.0).carbsPer100g(68.0).fatPer100g(1.5).build();

            productRepository.saveAll(List.of(
                    prod1, prod2, prod3, prod4, prod5, prod9, prod19, prod20,
                    prod21, prod24, prod34, prod40,
                    prod42, prod47, prod53, prod60
            ));
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
            Ingredient suero = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Suero"))
                    .findFirst().orElse(null);
            Ingredient peanut = ingredientRepository.findAll().stream()
                    .filter(i -> i.getName().contains("Pasta de Maní"))
                    .findFirst().orElse(null);
            if (suero != null) regularUser.getAllergens().add(suero);
            if (peanut != null) regularUser.getAllergens().add(peanut);
            userRepository.save(regularUser);
        }

        // 5. Sembrar Lotes y Trazabilidades si está vacío
        if (batchRepository.count() == 0) {
            System.out.println("Sembrando lotes y trazabilidades peruanos de prueba...");

            Product prod1 = productRepository.findAll().stream().filter(p -> p.getName().contains("Whey Pro")).findFirst().orElse(null);
            Product prod9 = productRepository.findAll().stream().filter(p -> p.getName().contains("Creatina")).findFirst().orElse(null);
            Product prod34 = productRepository.findAll().stream().filter(p -> p.getName().contains("Volt Maca")).findFirst().orElse(null);
            Product prod42 = productRepository.findAll().stream().filter(p -> p.getName().contains("Wild Protein")).findFirst().orElse(null);
            Product prod53 = productRepository.findAll().stream().filter(p -> p.getName().contains("Galleta Anti-Anémica")).findFirst().orElse(null);

            Ingredient wpc = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("WPC 80%")).findFirst().orElse(null);
            Ingredient soyLecithin = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("Lecitina de Soya")).findFirst().orElse(null);
            Ingredient creatine = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("Creatina Micronizada")).findFirst().orElse(null);
            Ingredient maca = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("Maca Amarilla")).findFirst().orElse(null);
            Ingredient caffeine = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("Cafeína Anhidra")).findFirst().orElse(null);
            Ingredient peanut = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("Pasta de Maní")).findFirst().orElse(null);
            Ingredient soyIsolate = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("Aislado de Proteína de Soya")).findFirst().orElse(null);
            Ingredient sangrecita = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("Sangrecita")).findFirst().orElse(null);
            Ingredient wheat = ingredientRepository.findAll().stream().filter(i -> i.getName().contains("Harina de Trigo")).findFirst().orElse(null);

            Supplier sup1 = supplierRepository.findAll().stream().filter(s -> s.getName().contains("Montana")).findFirst().orElse(null);
            Supplier sup2 = supplierRepository.findAll().stream().filter(s -> s.getName().contains("Ecoandino")).findFirst().orElse(null);
            Supplier sup3 = supplierRepository.findAll().stream().filter(s -> s.getName().contains("Química Suiza")).findFirst().orElse(null);
            Supplier sup5 = supplierRepository.findAll().stream().filter(s -> s.getName().contains("Alicorp")).findFirst().orElse(null);
            Supplier sup6 = supplierRepository.findAll().stream().filter(s -> s.getName().contains("Gloria")).findFirst().orElse(null);
            Supplier sup10 = supplierRepository.findAll().stream().filter(s -> s.getName().contains("Río Seco")).findFirst().orElse(null);

            if (prod1 != null && prod9 != null && prod34 != null && prod42 != null && prod53 != null) {
                // Lote 1: Whey Pro (Sabor Vainilla) (ACTIVE)
                Batch batch1 = Batch.builder()
                        .batchNumber("WP-VAN-001")
                        .productionDate(LocalDate.now().minusDays(10))
                        .expirationDate(LocalDate.now().plusYears(1))
                        .status(BatchStatus.ACTIVE)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/WP-VAN-001.png")
                        .product(prod1)
                        .build();

                // Lote 2: Creatina Monohidratada Micronizada Pura (ACTIVE)
                Batch batch2 = Batch.builder()
                        .batchNumber("CRE-MIC-002")
                        .productionDate(LocalDate.now().minusDays(15))
                        .expirationDate(LocalDate.now().plusYears(2))
                        .status(BatchStatus.ACTIVE)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/CRE-MIC-002.png")
                        .product(prod9)
                        .build();

                // Lote 3: Volt Maca (ACTIVE)
                Batch batch3 = Batch.builder()
                        .batchNumber("VOLT-MCA-003")
                        .productionDate(LocalDate.now().minusDays(5))
                        .expirationDate(LocalDate.now().plusMonths(6))
                        .status(BatchStatus.ACTIVE)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/VOLT-MCA-003.png")
                        .product(prod34)
                        .build();

                // Lote 4: Wild Protein Chocolate Maní (ACTIVE)
                Batch batch4 = Batch.builder()
                        .batchNumber("WILD-CHO-004")
                        .productionDate(LocalDate.now().minusDays(8))
                        .expirationDate(LocalDate.now().plusMonths(8))
                        .status(BatchStatus.ACTIVE)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/WILD-CHO-004.png")
                        .product(prod42)
                        .build();

                // Lote 5: Galleta Anti-Anémica (RECALLED)
                Batch batch5 = Batch.builder()
                        .batchNumber("NUTRIH-REC-005")
                        .productionDate(LocalDate.now().minusDays(30))
                        .expirationDate(LocalDate.now().plusMonths(6))
                        .status(BatchStatus.RECALLED)
                        .qrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/NUTRIH-REC-005.png")
                        .product(prod53)
                        .build();

                batchRepository.saveAll(List.of(batch1, batch2, batch3, batch4, batch5));

                // Guardar asociaciones
                // Lote 1
                BatchIngredient bi1 = BatchIngredient.builder().batch(batch1).ingredient(wpc).supplier(sup6).arrivalDate(LocalDate.now().minusDays(12)).freshnessStatus(FreshnessStatus.FRESH).build();
                BatchIngredient bi2 = BatchIngredient.builder().batch(batch1).ingredient(soyLecithin).supplier(sup3).arrivalDate(LocalDate.now().minusDays(12)).freshnessStatus(FreshnessStatus.FRESH).build();

                // Lote 2
                BatchIngredient bi3 = BatchIngredient.builder().batch(batch2).ingredient(creatine).supplier(sup1).arrivalDate(LocalDate.now().minusDays(20)).freshnessStatus(FreshnessStatus.FRESH).build();

                // Lote 3
                BatchIngredient bi4 = BatchIngredient.builder().batch(batch3).ingredient(maca).supplier(sup2).arrivalDate(LocalDate.now().minusDays(6)).freshnessStatus(FreshnessStatus.FRESH).build();
                BatchIngredient bi5 = BatchIngredient.builder().batch(batch3).ingredient(caffeine).supplier(sup1).arrivalDate(LocalDate.now().minusDays(6)).freshnessStatus(FreshnessStatus.FRESH).build();

                // Lote 4
                BatchIngredient bi6 = BatchIngredient.builder().batch(batch4).ingredient(peanut).supplier(sup2).arrivalDate(LocalDate.now().minusDays(9)).freshnessStatus(FreshnessStatus.FRESH).build();
                BatchIngredient bi7 = BatchIngredient.builder().batch(batch4).ingredient(soyIsolate).supplier(sup3).arrivalDate(LocalDate.now().minusDays(9)).freshnessStatus(FreshnessStatus.FRESH).build();

                // Lote 5
                BatchIngredient bi8 = BatchIngredient.builder().batch(batch5).ingredient(sangrecita).supplier(sup10).arrivalDate(LocalDate.now().minusDays(35)).freshnessStatus(FreshnessStatus.FRESH).build();
                BatchIngredient bi9 = BatchIngredient.builder().batch(batch5).ingredient(wheat).supplier(sup5).arrivalDate(LocalDate.now().minusDays(35)).freshnessStatus(FreshnessStatus.FRESH).build();

                batchIngredientRepository.saveAll(List.of(bi1, bi2, bi3, bi4, bi5, bi6, bi7, bi8, bi9));
            }
        }

        System.out.println("Siembra de base de datos NutriTrack Perú finalizada.");
    }
}
