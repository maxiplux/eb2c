package app.quantun.eb2c.service;


import app.quantun.eb2c.model.contract.request.ProductRequestDTO;
import app.quantun.eb2c.model.contract.response.ProductResponseDTO;
import app.quantun.eb2c.model.entity.bussines.Product;
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
