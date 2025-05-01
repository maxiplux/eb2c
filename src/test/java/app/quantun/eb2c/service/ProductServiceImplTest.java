package app.quantun.eb2c.service;

import app.quantun.eb2c.model.contract.request.ProductRequestDTO;
import app.quantun.eb2c.model.contract.request.ProductSearchCriteria;
import app.quantun.eb2c.model.contract.response.ProductResponseDTO;
import app.quantun.eb2c.model.entity.bussines.Product;
import app.quantun.eb2c.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");
        product1.setDescription("Test Description 1");
        product1.setPrice(BigDecimal.valueOf(100.00));
        product1.setInStock(true);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setDescription("Test Description 2");
        product2.setPrice(BigDecimal.valueOf(200.00));
        product2.setInStock(false);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Test Product");
        productRequestDTO.setDescription("Test Description");
        productRequestDTO.setPrice(BigDecimal.valueOf(100.00));
        productRequestDTO.setInStock(true);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(1L);
        productResponseDTO.setName("Test Product");
        productResponseDTO.setDescription("Test Description");
        productResponseDTO.setPrice(BigDecimal.valueOf(100.00));
        productResponseDTO.setInStock(true);
    }

    @Test
    void getAllProducts() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .thenReturn(productResponseDTO);

        // When
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Then
        assertEquals(2, result.size());
        verify(productRepository).findAll();
        verify(modelMapper, times(2)).map(any(Product.class), eq(ProductResponseDTO.class));
    }

    @Test
    void getProductById_whenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(modelMapper.map(product1, ProductResponseDTO.class)).thenReturn(productResponseDTO);

        // When
        Optional<ProductResponseDTO> result = productService.getProductById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(productResponseDTO, result.get());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_whenProductDoesNotExist() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<ProductResponseDTO> result = productService.getProductById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(productRepository).findById(99L);
    }

    @Test
    void createProduct() {
        // Given
        when(modelMapper.map(productRequestDTO, Product.class)).thenReturn(product1);
        when(productRepository.save(product1)).thenReturn(product1);
        when(modelMapper.map(product1, ProductResponseDTO.class)).thenReturn(productResponseDTO);

        // When
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        // Then
        assertEquals(productResponseDTO, result);
        verify(modelMapper).map(productRequestDTO, Product.class);
        verify(productRepository).save(product1);
        verify(modelMapper).map(product1, ProductResponseDTO.class);
    }

    @Test
    void updateProduct_whenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(product1)).thenReturn(product1);
        when(modelMapper.map(product1, ProductResponseDTO.class)).thenReturn(productResponseDTO);

        // When
        ProductResponseDTO result = productService.updateProduct(1L, productRequestDTO);

        // Then
        assertEquals(productResponseDTO, result);
        verify(productRepository).findById(1L);
        verify(productRepository).save(product1);
        verify(modelMapper).map(product1, ProductResponseDTO.class);
    }

    @Test
    void updateProduct_whenProductDoesNotExist() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> productService.updateProduct(99L, productRequestDTO));
        verify(productRepository).findById(99L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateProductFields() {
        // Given
        Product existingProduct = new Product();
        productRequestDTO.setName("Updated Name");
        productRequestDTO.setDescription("Updated Description");
        productRequestDTO.setPrice(BigDecimal.valueOf(150.00));
        productRequestDTO.setInStock(false);

        // When
        productService.updateProductFields(existingProduct, productRequestDTO);

        // Then
        assertEquals("Updated Name", existingProduct.getName());
        assertEquals("Updated Description", existingProduct.getDescription());
        assertEquals(BigDecimal.valueOf(150.00), existingProduct.getPrice());
        assertFalse(existingProduct.isInStock());
    }

    @Test
    void deleteProduct_whenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(product1);
    }

    @Test
    void deleteProduct_whenProductDoesNotExist() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(99L));
        verify(productRepository).findById(99L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void searchProductsByName() {
        // Given
        List<Product> products = Arrays.asList(product1);
        when(productRepository.findByNameContaining("Test")).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .thenReturn(productResponseDTO);

        // When
        List<ProductResponseDTO> result = productService.searchProductsByName("Test");

        // Then
        assertEquals(1, result.size());
        verify(productRepository).findByNameContaining("Test");
        verify(modelMapper).map(any(Product.class), eq(ProductResponseDTO.class));
    }

    @Test
    void getProductsUnderPrice() {
        // Given
        List<Product> products = Arrays.asList(product1);
        when(productRepository.findByPriceLessThan(BigDecimal.valueOf(150))).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .thenReturn(productResponseDTO);

        // When
        List<ProductResponseDTO> result = productService.getProductsUnderPrice(BigDecimal.valueOf(150));

        // Then
        assertEquals(1, result.size());
        verify(productRepository).findByPriceLessThan(BigDecimal.valueOf(150));
        verify(modelMapper).map(any(Product.class), eq(ProductResponseDTO.class));
    }

    @Test
    void getInStockProducts() {
        // Given
        List<Product> products = Arrays.asList(product1);
        when(productRepository.findByInStock(true)).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .thenReturn(productResponseDTO);

        // When
        List<ProductResponseDTO> result = productService.getInStockProducts();

        // Then
        assertEquals(1, result.size());
        verify(productRepository).findByInStock(true);
        verify(modelMapper).map(any(Product.class), eq(ProductResponseDTO.class));
    }

    @Test
    void findProductsByCriteria() {
        // Given
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        criteria.setNamePattern("Test");
        criteria.setMinPrice(BigDecimal.valueOf(50));
        criteria.setMaxPrice(BigDecimal.valueOf(150));
        criteria.setInStock(true);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product1), pageable, 1);

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .thenReturn(productResponseDTO);

        // When
        Page<ProductResponseDTO> result = productService.findProductsByCriteria(criteria, pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
        verify(modelMapper).map(any(Product.class), eq(ProductResponseDTO.class));
    }
}