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
     * Find all products
     * @return list of all products
     */
    public List<Product> findAllProducts() {
        return productRepository.listAll();
    }
    
    /**
     * Find a product by its ID
     * @param id the product ID
     * @return Optional containing the product if found
     */
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(productRepository.findById(id));
    }

    /**
     * Find a product by serial number
     * @param serialNumber the product serial number
     * @return Optional containing the product if found
     */
    public Optional<Product> findBySerialNumber(String serialNumber) {
        List<Product> products = productRepository.findBySerialNumber(serialNumber);
        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
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
     * Find products by name and model number
     * @param name the product name
     * @param modelNumber the model number
     * @return list of products
     */
    public List<Product> findByNameAndModelNumber(String name, String modelNumber) {
        return productRepository.findByNameAndModelNumber(name, modelNumber);
    }
    
    /**
     * Create a new product
     * @param product the product to create
     * @return the created product
     * @throws IllegalArgumentException if a product with the same serial number already exists
     */
    @Transactional
    public Product createProduct(Product product) {
        // Check if product with same serial number already exists
        if (product.getSerialNumber() != null && !product.getSerialNumber().isEmpty()) {
            Optional<Product> existingProduct = findBySerialNumber(product.getSerialNumber());
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
