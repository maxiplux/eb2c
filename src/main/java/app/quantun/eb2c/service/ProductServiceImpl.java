package app.quantun.b2b.service;


import app.quantun.b2b.model.contract.request.ProductRequestDTO;
import app.quantun.b2b.model.contract.response.ProductResponseDTO;
import app.quantun.b2b.model.entity.bussines.Product;
import app.quantun.b2b.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing products.
 * This class provides methods for CRUD operations on products.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    private final ModelMapper modelMapper;

    /**
     * Retrieve a list of all products.
     *
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific product by its ID.
     *
     * @param id the ID of the product
     * @return an Optional containing the ProductResponseDTO if found, otherwise empty
     */
    @Override
    public Optional<ProductResponseDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(product -> modelMapper.map(product, ProductResponseDTO.class));
    }

    /**
     * Add a new product to the system.
     *
     * @param productRequestDTO the details of the product to be created
     * @return the created ProductResponseDTO
     */
    @Transactional
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = modelMapper.map(productRequestDTO, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    /**
     * Update details of an existing product.
     *
     * @param id the ID of the product to be updated
     * @param productRequestDTO the updated product details
     * @return the updated ProductResponseDTO
     */
    @Transactional
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    updateProductFields(existingProduct, productRequestDTO);
                    Product updatedProduct = productRepository.save(existingProduct);
                    return modelMapper.map(updatedProduct, ProductResponseDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    /**
     * Update the fields of an existing product with the provided details.
     *
     * @param existingProduct the existing product to be updated
     * @param productRequestDTO the updated product details
     */
    @Override
    public void updateProductFields(Product existingProduct, ProductRequestDTO productRequestDTO) {
        if (productRequestDTO.getName() != null) {
            existingProduct.setName(productRequestDTO.getName());
        }
        if (productRequestDTO.getDescription() != null) {
            existingProduct.setDescription(productRequestDTO.getDescription());
        }
        if (productRequestDTO.getPrice() != null) {
            existingProduct.setPrice(productRequestDTO.getPrice());
        }
        existingProduct.setInStock(productRequestDTO.isInStock());
    }

    /**
     * Remove a product from the system.
     *
     * @param id the ID of the product to be deleted
     */
    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
        productRepository.delete(product);
    }

    /**
     * Find products containing the given name.
     *
     * @param name the name to search for
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name).stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve products priced below a given value.
     *
     * @param price the maximum price
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> getProductsUnderPrice(BigDecimal price) {
        return productRepository.findByPriceLessThan(price).stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve all products that are currently in stock.
     *
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> getInStockProducts() {
        return productRepository.findByInStock(true).stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }
}
