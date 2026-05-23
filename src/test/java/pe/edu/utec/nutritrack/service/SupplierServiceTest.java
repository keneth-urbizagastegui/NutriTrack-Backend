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
import pe.edu.utec.nutritrack.dto.request.SupplierRequest;
import pe.edu.utec.nutritrack.dto.response.SupplierResponse;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.mapper.SupplierMapper;
import pe.edu.utec.nutritrack.model.Supplier;
import pe.edu.utec.nutritrack.repository.SupplierRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    @Test
    void createSupplier_Success() {
        // Given
        SupplierRequest request = SupplierRequest.builder()
                .name("Eco Farms")
                .contactEmail("contact@ecofarms.com")
                .isActive(true)
                .build();

        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("Eco Farms")
                .contactEmail("contact@ecofarms.com")
                .isActive(true)
                .build();

        SupplierResponse expectedResponse = SupplierResponse.builder()
                .id(1L)
                .name("Eco Farms")
                .contactEmail("contact@ecofarms.com")
                .isActive(true)
                .build();

        when(supplierRepository.existsByName("Eco Farms")).thenReturn(false);
        when(supplierMapper.toEntity(request)).thenReturn(supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toResponse(supplier)).thenReturn(expectedResponse);

        // When
        SupplierResponse result = supplierService.createSupplier(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Eco Farms");
        verify(supplierRepository).save(supplier);
    }

    @Test
    void createSupplier_DuplicateName_ThrowsException() {
        // Given
        SupplierRequest request = SupplierRequest.builder()
                .name("Eco Farms")
                .build();

        when(supplierRepository.existsByName("Eco Farms")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> supplierService.createSupplier(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya existe");
    }

    @Test
    void getSupplierById_Success() {
        // Given
        Supplier supplier = Supplier.builder().id(1L).name("Eco Farms").build();
        SupplierResponse response = SupplierResponse.builder().id(1L).name("Eco Farms").build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponse(supplier)).thenReturn(response);

        // When
        SupplierResponse result = supplierService.getSupplierById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Eco Farms");
    }

    @Test
    void getSupplierById_NotFound_ThrowsException() {
        // Given
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> supplierService.getSupplierById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    void getAllSuppliers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Supplier supplier = Supplier.builder().id(1L).name("Eco Farms").build();
        SupplierResponse response = SupplierResponse.builder().id(1L).name("Eco Farms").build();
        Page<Supplier> page = new PageImpl<>(List.of(supplier), pageable, 1);

        when(supplierRepository.findAll(pageable)).thenReturn(page);
        when(supplierMapper.toResponse(supplier)).thenReturn(response);

        // When
        Page<SupplierResponse> result = supplierService.getAllSuppliers(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Eco Farms");
    }
}
