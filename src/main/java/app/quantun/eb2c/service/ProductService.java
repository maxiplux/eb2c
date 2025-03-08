package app.quantun.b2b.service;


import app.quantun.b2b.model.contract.request.ProductRequestDTO;
import app.quantun.b2b.model.contract.response.ProductResponseDTO;
import app.quantun.b2b.model.entity.bussines.Product;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponseDTO> getAllProducts();

    Optional<ProductResponseDTO> getProductById(Long id);

    @Transactional
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    @Transactional
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);

    void updateProductFields(Product existingProduct, ProductRequestDTO productRequestDTO);

    @Transactional
    void deleteProduct(Long id);

    List<ProductResponseDTO> searchProductsByName(String name);

    List<ProductResponseDTO> getProductsUnderPrice(BigDecimal price);

    List<ProductResponseDTO> getInStockProducts();
}
