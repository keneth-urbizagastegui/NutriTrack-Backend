package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.utec.nutritrack.dto.request.ProductRequest;
import pe.edu.utec.nutritrack.dto.response.ProductResponse;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.mapper.ProductMapper;
import pe.edu.utec.nutritrack.model.Product;
import pe.edu.utec.nutritrack.repository.ProductRepository;
import pe.edu.utec.nutritrack.specification.ProductSpecification;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return addLinks(productMapper.toResponse(savedProduct));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(String name, Double minProtein, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasProteinGreaterThanOrEqual(minProtein));

        return productRepository.findAll(spec, pageable)
                .map(product -> addLinks(productMapper.toResponse(product)));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con ID " + id + " no existe."));
        return addLinks(productMapper.toResponse(product));
    }

    private ProductResponse addLinks(ProductResponse response) {
        try {
            String selfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/products/{id}")
                    .buildAndExpand(response.getId())
                    .toUriString();
            response.set_links(Map.of("self", Map.of("href", selfUrl)));
        } catch (Exception e) {
            response.set_links(Map.of("self", Map.of("href", "http://localhost:8080/api/v1/products/" + response.getId())));
        }
        return response;
    }
}
