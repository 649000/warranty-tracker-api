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
     * Find a product by model number
     * @param modelNumber the product model number
     * @return Optional containing the product if found
     */
    public Optional<Product> findByModelNumber(String modelNumber) {
        List<Product> products = productRepository.findByModelNumber(modelNumber);
        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
    }
    
    /**
     * Search products by multiple criteria
     * @param name the product name to search for (partial match)
     * @param brand the brand to search for (partial match)
     * @param modelNumber the model number to search for (partial match)
     * @return list of products matching all provided criteria
     */
    public List<Product> searchProducts(String name, String brand, String modelNumber) {
        // If no search parameters provided, return all products
        if ((name == null || name.trim().isEmpty()) && 
            (brand == null || brand.trim().isEmpty()) && 
            (modelNumber == null || modelNumber.trim().isEmpty())) {
            return findAllProducts();
        }
        
        return productRepository.searchProducts(name, brand, modelNumber);
    }
    
    /**
     * Create a new product
     * @param product the product to create
     * @return the created product
     * @throws IllegalArgumentException if a product with the same model number already exists or validation fails
     */
    @Transactional
    public Product createProduct(Product product) {
        // Validate required fields
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        
        if (product.getModelNumber() == null || product.getModelNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Model number is required");
        }
        
        // Check if product with same model number already exists
        Optional<Product> existingProduct = findByModelNumber(product.getModelNumber().trim());
        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException("Product with this model number already exists");
        }

        // Trim whitespace from fields
        product.setName(product.getName().trim());
        product.setModelNumber(product.getModelNumber().trim());
        if (product.getBrand() != null) {
            product.setBrand(product.getBrand().trim());
        }

        return productRepository.createProduct(product);
    }
    
    /**
     * Update an existing product
     * @param product the product to update
     * @return the updated product
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public Product updateProduct(Product product) {
        // Validate required fields
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        
        if (product.getModelNumber() == null || product.getModelNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Model number is required");
        }
        
        // Trim whitespace from fields
        product.setName(product.getName().trim());
        product.setModelNumber(product.getModelNumber().trim());
        if (product.getBrand() != null) {
            product.setBrand(product.getBrand().trim());
        }

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
