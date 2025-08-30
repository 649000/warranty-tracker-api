package com.nazri.service;

import com.nazri.model.Product;
import com.nazri.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductService {
    
    @Inject
    ProductRepository productRepository;
    
    /**
     * Find a product by its ID
     * @param id the product ID
     * @return Optional containing the product if found
     */
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    
    /**
     * Find a product by serial number
     * @param serialNumber the product serial number
     * @return Optional containing the product if found
     */
    public Optional<Product> findBySerialNumber(String serialNumber) {
        return productRepository.findBySerialNumber(serialNumber);
    }
    
    /**
     * Find products by name (partial match)
     * @param name the product name to search for
     * @return list of products
     */
    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }
    
    /**
     * Find products by brand (partial match)
     * @param brand the brand to search for
     * @return list of products
     */
    public List<Product> findByBrandContaining(String brand) {
        return productRepository.findByBrandContaining(brand);
    }
    
    /**
     * Find products by model number (partial match)
     * @param modelNumber the model number to search for
     * @return list of products
     */
    public List<Product> findByModelNumberContaining(String modelNumber) {
        return productRepository.findByModelNumberContaining(modelNumber);
    }
    
    /**
     * Create a new product
     * @param product the product to create
     * @return the created product
     */
    @Transactional
    public Product createProduct(Product product) {
        // Check if product with same serial number already exists
        if (product.getSerialNumber() != null && !product.getSerialNumber().isEmpty()) {
            Optional<Product> existingProduct = productRepository.findBySerialNumber(product.getSerialNumber());
            if (existingProduct.isPresent()) {
                throw new IllegalArgumentException("Product with this serial number already exists");
            }
        }
        
        return productRepository.createProduct(product);
    }
    
    /**
     * Update an existing product
     * @param product the product to update
     * @return the updated product
     */
    @Transactional
    public Product updateProduct(Product product) {
        return productRepository.updateProduct(product);
    }
    
    /**
     * Delete a product by ID
     * @param id the product ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteProduct(id);
    }
}
