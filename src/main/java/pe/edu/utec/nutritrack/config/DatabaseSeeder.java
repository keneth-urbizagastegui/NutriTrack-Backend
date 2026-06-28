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
    private final ConsumptionLogRepository consumptionLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Iniciando revisión de siembra de datos NutriTrack Perú...");

        // 1. Limpieza condicional en cascada si detectamos tablas incompletas/antiguas
        boolean forceResetProducts = productRepository.count() > 0 && productRepository.count() < 60;
        boolean forceResetIngredients = ingredientRepository.count() > 0 && ingredientRepository.count() < 23;
        boolean forceResetSuppliers = supplierRepository.count() > 0 && supplierRepository.count() < 10;

        if (forceResetProducts || forceResetIngredients || forceResetSuppliers) {
            System.out.println("Semilla de datos incompleta o antigua detectada. Iniciando limpieza en cascada...");
            
            // Limpiar logs de consumo que dependen de los lotes
            consumptionLogRepository.deleteAll();
            
            // Limpiar alérgenos de los usuarios para liberar llaves foráneas de ingredientes
            for (User u : userRepository.findAll()) {
                u.getAllergens().clear();
                userRepository.save(u);
            }
            
            // Limpiar relaciones de lotes-ingredientes-proveedores
            batchIngredientRepository.deleteAll();
            batchRepository.deleteAll();
            
            if (forceResetProducts) {
                productRepository.deleteAll();
            }
            if (forceResetIngredients) {
                ingredientRepository.deleteAll();
            }
            if (forceResetSuppliers) {
                supplierRepository.deleteAll();
            }
            
            System.out.println("Limpieza en cascada finalizada con éxito.");
        }

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
            System.out.println("Sembrando catálogo de 60 productos peruanos...");
            // SUPPLEMENT (1-20)
            Product prod1 = Product.builder().name("Whey Pro (Sabor Vainilla)").brand("Universe Nutrition").description("Proteína de suero de alta calidad con L-Glutamina y vitaminas, ideal para post-entrenamiento.").category(ProductCategory.SUPPLEMENT).proteinPer100g(75.0).carbsPer100g(10.0).fatPer100g(4.0).build();
            Product prod2 = Product.builder().name("BigM (Sabor Chocolate)").brand("Universe Nutrition").description("Ganador de masa muscular avanzado con avena, proteínas de suero y soya.").category(ProductCategory.SUPPLEMENT).proteinPer100g(25.0).carbsPer100g(60.0).fatPer100g(5.0).build();
            Product prod3 = Product.builder().name("Iso Whey 90 (Sabor Fresa)").brand("Universe Nutrition").description("Aislado de proteína de suero al 90%, cero grasas y carbohidratos, rápida absorción.").category(ProductCategory.SUPPLEMENT).proteinPer100g(90.0).carbsPer100g(0.5).fatPer100g(0.2).build();
            Product prod4 = Product.builder().name("Premium 100% Whey (Vainilla)").brand("Lab Nutrition").description("Suplemento hiperproteico con mezcla de concentrado y aislado de suero lácteo.").category(ProductCategory.SUPPLEMENT).proteinPer100g(78.0).carbsPer100g(8.0).fatPer100g(3.5).build();
            Product prod5 = Product.builder().name("Pump Kong Pre-Entrenamiento").brand("Lab Nutrition").description("Fórmula de pre-entrenamiento explosiva con cafeína, beta-alanina y citrulina.").category(ProductCategory.SUPPLEMENT).proteinPer100g(5.0).carbsPer100g(20.0).fatPer100g(0.0).build();
            Product prod6 = Product.builder().name("Gold Standard 100% Whey (Chocolate)").brand("Optimum Nutrition").description("La proteína de suero de leche más vendida del mundo, compuesta por aislados de proteína de suero.").category(ProductCategory.SUPPLEMENT).proteinPer100g(77.4).carbsPer100g(10.0).fatPer100g(3.2).build();
            Product prod7 = Product.builder().name("ISO 100 Hydrolyzed (Gourmet Chocolate)").brand("Dymatize").description("Aislado de proteína de suero hidrolizado y ultrafiltrado, libre de gluten y lactosa.").category(ProductCategory.SUPPLEMENT).proteinPer100g(83.3).carbsPer100g(3.3).fatPer100g(1.7).build();
            Product prod8 = Product.builder().name("Metapure Whey Isolate (Fresa-Plátano)").brand("QNT").description("Aislado de proteína ultra puro elaborado exclusivamente a partir de suero lácteo sin carbohidratos.").category(ProductCategory.SUPPLEMENT).proteinPer100g(88.0).carbsPer100g(1.5).fatPer100g(1.1).build();
            Product prod9 = Product.builder().name("Creatina Monohidratada Micronizada Pura").brand("Universe Nutrition").description("Creatina micronizada pura al 100% para el incremento de la fuerza, potencia muscular y volumen.").category(ProductCategory.SUPPLEMENT).proteinPer100g(0.0).carbsPer100g(0.0).fatPer100g(0.0).build();
            Product prod10 = Product.builder().name("Collagen Pro (Hidrolizado)").brand("Universe Nutrition").description("Colágeno bovino hidrolizado premium con vitamina C, diseñado para articulaciones, piel y tendones.").category(ProductCategory.SUPPLEMENT).proteinPer100g(90.0).carbsPer100g(0.0).fatPer100g(0.0).build();
            Product prod11 = Product.builder().name("Collagen Fit con L-Carnitina").brand("FitFem").description("Colágeno hidrolizado formulado para mujeres con L-Carnitina para apoyar la quema de grasa.").category(ProductCategory.SUPPLEMENT).proteinPer100g(85.0).carbsPer100g(2.0).fatPer100g(0.0).build();
            Product prod12 = Product.builder().name("Pro BCAA 6000 (Sabor Limón)").brand("Universe Nutrition").description("Aminoácidos ramificados de cadena ratio 2:1:1 con glutamina para la recuperación muscular.").category(ProductCategory.SUPPLEMENT).proteinPer100g(75.0).carbsPer100g(5.0).fatPer100g(0.0).build();
            Product prod13 = Product.builder().name("Psychotic Pre-Workout").brand("Insane Labz").description("Fórmula de pre-entreno extrema con alta dosis de cafeína y beta-alanina para enfoque de túnel.").category(ProductCategory.SUPPLEMENT).proteinPer100g(0.0).carbsPer100g(25.0).fatPer100g(0.0).build();
            Product prod14 = Product.builder().name("Soja Complex (Aislado de Soya)").brand("Universe Nutrition").description("Proteína de origen vegetal 100% a base de aislado de soya fortificado con aminoácidos y hierro.").category(ProductCategory.SUPPLEMENT).proteinPer100g(80.0).carbsPer100g(8.0).fatPer100g(1.5).build();
            Product prod15 = Product.builder().name("Impact Whey Isolate (Sin Sabor)").brand("Myprotein").description("Aislado de proteína de suero puro sin saborizantes ni colorantes, ideal para batidos neutros.").category(ProductCategory.SUPPLEMENT).proteinPer100g(90.0).carbsPer100g(2.5).fatPer100g(0.3).build();
            Product prod16 = Product.builder().name("L-Citrulline Malate Pura").brand("Nutricost").description("Vasodilatador puro de L-Citrulina Malato para mejorar el flujo de oxígeno y bombeo muscular.").category(ProductCategory.SUPPLEMENT).proteinPer100g(0.0).carbsPer100g(0.0).fatPer100g(0.0).build();
            Product prod17 = Product.builder().name("Super Mass Gainer (Vainilla)").brand("Dymatize").description("Ganador de peso masivo con alta concentración de calorías, proteínas de suero, caseína y dextrosa.").category(ProductCategory.SUPPLEMENT).proteinPer100g(15.0).carbsPer100g(75.0).fatPer100g(3.0).build();
            Product prod18 = Product.builder().name("Mega Creatina Creapure").brand("ADN").description("Creatina monohidratada de calidad farmacéutica certificada Creapure, libre de impurezas.").category(ProductCategory.SUPPLEMENT).proteinPer100g(0.0).carbsPer100g(0.0).fatPer100g(0.0).build();
            Product prod19 = Product.builder().name("Maca Amarilla Orgánica Gelatinizada").brand("Ecoandino").description("Suplemento energético andino ancestral que regula el sistema endocrino y mejora la resistencia.").category(ProductCategory.SUPPLEMENT).proteinPer100g(12.0).carbsPer100g(65.0).fatPer100g(1.2).build();
            Product prod20 = Product.builder().name("Cacao Orgánico Fino de Aroma").brand("Ecoandino").description("Superalimento rico en polifenoles, antioxidantes naturales y teobromina para energía limpia.").category(ProductCategory.SUPPLEMENT).proteinPer100g(19.5).carbsPer100g(45.0).fatPer100g(12.0).build();

            // BEVERAGE (21-40)
            Product prod21 = Product.builder().name("Sporade Tropical").brand("AJE").description("Bebida isotónica rehidratante con electrolitos y sabor tropical, de rápida absorción.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(4.88).fatPer100g(0.0).build();
            Product prod22 = Product.builder().name("Sporade Blueberry").brand("AJE").description("Rehidratante con sodio, potasio y sabor a mora azul, optimiza el balance de fluidos.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(4.88).fatPer100g(0.0).build();
            Product prod23 = Product.builder().name("Sporade Apple Ice (Sin Azúcar)").brand("AJE").description("Bebida rehidratante sabor manzana verde sin azúcar añadida, endulzada con sucralosa.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(0.5).fatPer100g(0.0).build();
            Product prod24 = Product.builder().name("Gatorade Tropical").brand("PepsiCo").description("Bebida deportiva científicamente formulada con carbohidratos simples y electrolitos clave.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(6.0).fatPer100g(0.0).build();
            Product prod25 = Product.builder().name("Gatorade Mandarina").brand("PepsiCo").description("Formulación rehidratante isotónica sabor mandarina cítrica, repone sodio y potasio perdidos.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(6.0).fatPer100g(0.0).build();
            Product prod26 = Product.builder().name("Gatorade Maracuyá").brand("PepsiCo").description("Sabor exclusivo para el mercado peruano de fruta de la pasión, ideal para climas cálidos.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(6.0).fatPer100g(0.0).build();
            Product prod27 = Product.builder().name("Powerade Mora Azul").brand("Coca-Cola").description("Bebida hidratante con el sistema de 4 electrolitos (ION4) que ayuda a reponer el sudor.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(5.8).fatPer100g(0.0).build();
            Product prod28 = Product.builder().name("Powerade Multifrutas").brand("Coca-Cola").description("Sabor frutal intenso con sodio, potasio, calcio y magnesio para evitar calambres.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(5.8).fatPer100g(0.0).build();
            Product prod29 = Product.builder().name("Electrolight Fresa").brand("Lindley").description("Bebida ligera sabor fresa con bajo aporte calórico y un balance óptimo de electrolitos.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(2.0).fatPer100g(0.0).build();
            Product prod30 = Product.builder().name("Electrolight Piña").brand("Lindley").description("Refrescante sabor piña sin azúcares pesados, formulado para hidratación diaria en deportistas.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(2.0).fatPer100g(0.0).build();
            Product prod31 = Product.builder().name("Electrolife Fresa-Kiwi").brand("Abbott").description("Bebida rehidratante de grado farmacéutico con zinc y electrolitos esenciales.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(3.5).fatPer100g(0.0).build();
            Product prod32 = Product.builder().name("Electrolife Maracuyá").brand("Abbott").description("Solución isotónica sabor maracuyá de óptima osmolalidad para deshidratación.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(3.5).fatPer100g(0.0).build();
            Product prod33 = Product.builder().name("Dilyte Fresa-Kiwi").brand("Medifarma").description("Bebida con 10 iones esenciales y baja en calorías para deportistas de alto rendimiento.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(1.8).fatPer100g(0.0).build();
            Product prod34 = Product.builder().name("Volt Maca (Energizante)").brand("AJE").description("Bebida energizante que fusiona taurina y cafeína con extracto natural de Maca andina.").category(ProductCategory.BEVERAGE).proteinPer100g(0.2).carbsPer100g(11.0).fatPer100g(0.0).build();
            Product prod35 = Product.builder().name("Volt Yellow").brand("AJE").description("Energizante de alto impacto con vitaminas del complejo B, taurina y cafeína para enfoque físico.").category(ProductCategory.BEVERAGE).proteinPer100g(0.1).carbsPer100g(11.5).fatPer100g(0.0).build();
            Product prod36 = Product.builder().name("Volt Blueberry Maca").brand("AJE").description("Fórmula energética sabor arándano con los beneficios vigorizantes de la maca negra peruana.").category(ProductCategory.BEVERAGE).proteinPer100g(0.2).carbsPer100g(11.2).fatPer100g(0.0).build();
            Product prod37 = Product.builder().name("Red Bull Original").brand("Red Bull").description("Bebida funcional que revitaliza cuerpo y mente con cafeína de alta pureza y taurina.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(11.0).fatPer100g(0.0).build();
            Product prod38 = Product.builder().name("Monster Energy Ultra (Blanca)").brand("Monster").description("Energizante icónico sin azúcar, sabor cítrico refrescante, ideal para dietas de definición.").category(ProductCategory.BEVERAGE).proteinPer100g(0.0).carbsPer100g(0.9).fatPer100g(0.0).build();
            Product prod39 = Product.builder().name("Emoliente Rehidratante Sport").brand("Andean Form").description("Bebida deportiva basada en el emoliente tradicional, con linaza, cebada, limón y sales minerales.").category(ProductCategory.BEVERAGE).proteinPer100g(0.5).carbsPer100g(5.0).fatPer100g(0.1).build();
            Product prod40 = Product.builder().name("Chicha Morada Antiox Sport").brand("Andean Form").description("Bebida hidratante rica en antioxidantes provenientes del maíz morado, con limón y sal de Maras.").category(ProductCategory.BEVERAGE).proteinPer100g(0.1).carbsPer100g(6.2).fatPer100g(0.0).build();

            // READY_MEAL (41-60)
            Product prod41 = Product.builder().name("Wild Protein Lemon Pie").brand("Wild Foods").description("Barra de proteína de soya y suero de leche, sabor pie de limón, sin sellos ni azúcares añadidos.").category(ProductCategory.READY_MEAL).proteinPer100g(32.2).carbsPer100g(24.4).fatPer100g(8.6).build();
            Product prod42 = Product.builder().name("Wild Protein Chocolate Maní").brand("Wild Foods").description("Barra proteica crujiente con pasta de maní, cacao fino de aroma y proteína de soya.").category(ProductCategory.READY_MEAL).proteinPer100g(33.0).carbsPer100g(22.0).fatPer100g(10.0).build();
            Product prod43 = Product.builder().name("Crunchy Protein Bar Chocolate").brand("QNT").description("Barra de proteína crujiente con 30% de proteína de suero, recubierta de chocolate de leche.").category(ProductCategory.READY_MEAL).proteinPer100g(30.0).carbsPer100g(32.0).fatPer100g(12.0).build();
            Product prod44 = Product.builder().name("Wafer Protein Bar Chocolate").brand("QNT").description("Snack proteico tipo oblea cubierto de cacao, perfecto para meriendas rápidas post-entrenamiento.").category(ProductCategory.READY_MEAL).proteinPer100g(32.0).carbsPer100g(35.0).fatPer100g(15.0).build();
            Product prod45 = Product.builder().name("Protein Brownie Chocolate Chip").brand("Myprotein").description("Brownie horneado blando y delicioso con pepitas de chocolate, alto contenido en proteína láctea.").category(ProductCategory.READY_MEAL).proteinPer100g(23.0).carbsPer100g(41.0).fatPer100g(9.7).build();
            Product prod46 = Product.builder().name("Crispy Protein Wafer Vainilla").brand("Myprotein").description("Galleta tipo oblea rellena con crema de vainilla proteica, baja en carbohidratos activos.").category(ProductCategory.READY_MEAL).proteinPer100g(30.0).carbsPer100g(28.0).fatPer100g(14.0).build();
            Product prod47 = Product.builder().name("Barra de Quinua y Maní").brand("Mamalama").description("Barra de cereales andinos naturales elaborada con quinua pop, maní tostado y endulzada de forma natural.").category(ProductCategory.READY_MEAL).proteinPer100g(12.0).carbsPer100g(52.0).fatPer100g(14.0).build();
            Product prod48 = Product.builder().name("Barra de Cacao y Maca").brand("Mamalama").description("Barra energética andina formulada con cacao orgánico del Vraem, maca amarilla y quinua extruida.").category(ProductCategory.READY_MEAL).proteinPer100g(10.0).carbsPer100g(55.0).fatPer100g(8.0).build();
            Product prod49 = Product.builder().name("Barra de Aguaymanto y Linaza").brand("Mamalama").description("Snack saludable con trozos de aguaymanto ácido deshidratado, linaza molida y kiwicha pop.").category(ProductCategory.READY_MEAL).proteinPer100g(8.5).carbsPer100g(58.0).fatPer100g(6.2).build();
            Product prod50 = Product.builder().name("Barra de Proteína Chocolate").brand("Fit Rex").description("Barra nacional alta en proteínas de suero y soya con una deliciosa cobertura de cacao sin azúcar.").category(ProductCategory.READY_MEAL).proteinPer100g(30.0).carbsPer100g(25.0).fatPer100g(9.0).build();
            Product prod51 = Product.builder().name("Barra de Proteína Café Latte").brand("Fit Rex").description("Barra energética proteica sabor café espresso con notas lácteas, ideal como snack pre-workout.").category(ProductCategory.READY_MEAL).proteinPer100g(31.0).carbsPer100g(24.0).fatPer100g(8.5).build();
            Product prod52 = Product.builder().name("Chips de Camote con Sal de Maras").brand("Tikas").description("Hojuelas crujientes de camote amarillo nativo frito, sazonadas con sal pura de las salineras de Maras.").category(ProductCategory.READY_MEAL).proteinPer100g(2.1).carbsPer100g(62.0).fatPer100g(22.0).build();
            Product prod53 = Product.builder().name("Galleta Anti-Anémica y Proteica Clásica").brand("Nutri H").description("Famosa galleta peruana contra la anemia, enriquecida con sangre bovina (hierro hemínico), quinua y cacao.").category(ProductCategory.READY_MEAL).proteinPer100g(14.5).carbsPer100g(63.3).fatPer100g(12.5).build();
            Product prod54 = Product.builder().name("Quinoa Cup Kiwicha & Manzana").brand("Andean Form").description("Taza de avena, quinua y kiwicha instantánea con cubos de manzana deshidratada lista para consumir.").category(ProductCategory.READY_MEAL).proteinPer100g(8.0).carbsPer100g(65.0).fatPer100g(3.5).build();
            Product prod55 = Product.builder().name("Protein Bar Hazelnut Praline").brand("Twentys").description("Barra ultra baja en carbohidratos con aislado de proteína y un cremoso relleno de avellana.").category(ProductCategory.READY_MEAL).proteinPer100g(33.3).carbsPer100g(10.0).fatPer100g(11.5).build();
            Product prod56 = Product.builder().name("Protein Snack Banana Chips & Caramel").brand("Tottus").description("Mezcla de plátano deshidratado crujiente con trozos de barra de caramelo proteico.").category(ProductCategory.READY_MEAL).proteinPer100g(15.0).carbsPer100g(52.0).fatPer100g(9.0).build();
            Product prod57 = Product.builder().name("Protein Snack Berries & White Glaze").brand("Tottus").description("Snack mixto de arándanos, aguaymanto deshidratado y bocaditos de proteína glaseados.").category(ProductCategory.READY_MEAL).proteinPer100g(16.0).carbsPer100g(48.0).fatPer100g(10.5).build();
            Product prod58 = Product.builder().name("Barra de Cereales con Kiwicha y Avena").brand("Unión").description("Barra de cereales andinos naturales rica en fibra y kiwicha pop.").category(ProductCategory.READY_MEAL).proteinPer100g(6.5).carbsPer100g(68.0).fatPer100g(4.0).build();
            Product prod59 = Product.builder().name("Krak Oat & Peanut Butter Protein Bar").brand("Demolitor").description("Barra proteica con espirulina, avena integral, pasta de maní y endulzada con estevia.").category(ProductCategory.READY_MEAL).proteinPer100g(28.0).carbsPer100g(38.0).fatPer100g(11.0).build();
            Product prod60 = Product.builder().name("Snacks de Aguaymanto Deshidratado Orgánico").brand("Ecoandino").description("Aguaymanto deshidratado 100% orgánico y certificado.").category(ProductCategory.READY_MEAL).proteinPer100g(3.0).carbsPer100g(68.0).fatPer100g(1.5).build();

            productRepository.saveAll(List.of(
                    prod1, prod2, prod3, prod4, prod5, prod6, prod7, prod8, prod9, prod10,
                    prod11, prod12, prod13, prod14, prod15, prod16, prod17, prod18, prod19, prod20,
                    prod21, prod22, prod23, prod24, prod25, prod26, prod27, prod28, prod29, prod30,
                    prod31, prod32, prod33, prod34, prod35, prod36, prod37, prod38, prod39, prod40,
                    prod41, prod42, prod43, prod44, prod45, prod46, prod47, prod48, prod49, prod50,
                    prod51, prod52, prod53, prod54, prod55, prod56, prod57, prod58, prod59, prod60
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
