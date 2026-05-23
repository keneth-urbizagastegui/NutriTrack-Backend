package pe.edu.utec.nutritrack.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pe.edu.utec.nutritrack.dto.request.ProductRequest;
import pe.edu.utec.nutritrack.dto.response.ProductResponse;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.mapper.ProductMapper;
import pe.edu.utec.nutritrack.model.Product;
import pe.edu.utec.nutritrack.model.ProductCategory;
import pe.edu.utec.nutritrack.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_Success() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Whey Protein")
                .brand("NutriBrand")
                .category(ProductCategory.SUPPLEMENT)
                .proteinPer100g(80.0)
                .carbsPer100g(5.0)
                .fatPer100g(2.0)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Whey Protein")
                .brand("NutriBrand")
                .category(ProductCategory.SUPPLEMENT)
                .proteinPer100g(80.0)
                .carbsPer100g(5.0)
                .fatPer100g(2.0)
                .build();

        ProductResponse expectedResponse = ProductResponse.builder()
                .id(1L)
                .name("Whey Protein")
                .brand("NutriBrand")
                .category(ProductCategory.SUPPLEMENT)
                .proteinPer100g(80.0)
                .carbsPer100g(5.0)
                .fatPer100g(2.0)
                .build();

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        // When
        ProductResponse result = productService.createProduct(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Whey Protein");
        assertThat(result.getProteinPer100g()).isEqualTo(80.0);
        verify(productRepository).save(product);
    }

    @Test
    void getProductById_Success() {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("Oats")
                .brand("Brand")
                .category(ProductCategory.READY_MEAL)
                .build();

        ProductResponse expectedResponse = ProductResponse.builder()
                .id(1L)
                .name("Oats")
                .brand("Brand")
                .category(ProductCategory.READY_MEAL)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        // When
        ProductResponse result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Oats");
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no existe");
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllProducts_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Product product = Product.builder().id(1L).name("Oats").build();
        ProductResponse response = ProductResponse.builder().id(1L).name("Oats").build();

        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toResponse(product)).thenReturn(response);

        // When
        Page<ProductResponse> result = productService.getAllProducts(null, null, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Oats");
    }
}
