package com.nazri.repository;

import com.nazri.model.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    
    /**
     * Find a product by its ID
     * @param id the product ID
     * @return Optional containing the product if found
     */
    public Optional<Product> findById(Long id) {
        return findByIdOptional(id);
    }
    
    /**
     * Find a product by serial number
     * @param serialNumber the product serial number
     * @return Optional containing the product if found
     */
    public Optional<Product> findBySerialNumber(String serialNumber) {
        return find("serialNumber", serialNumber).firstResultOptional();
    }
    
    /**
     * Find products by name (partial match)
     * @param name the product name to search for
     * @return list of products
     */
    public List<Product> findByNameContaining(String name) {
        return find("name like ?1", "%" + name + "%").list();
    }
    
    /**
     * Find products by brand (partial match)
     * @param brand the brand to search for
     * @return list of products
     */
    public List<Product> findByBrandContaining(String brand) {
        return find("brand like ?1", "%" + brand + "%").list();
    }
    
    /**
     * Find products by model number (partial match)
     * @param modelNumber the model number to search for
     * @return list of products
     */
    public List<Product> findByModelNumberContaining(String modelNumber) {
        return find("modelNumber like ?1", "%" + modelNumber + "%").list();
    }
    
    /**
     * Create a new product
     * @param product the product to create
     * @return the created product
     */
    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        persistAndFlush(product);
        return product;
    }
    
    /**
     * Update an existing product
     * @param product the product to update
     * @return the updated product
     */
    public Product updateProduct(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        getEntityManager().merge(product);
        return product;
    }
    
    /**
     * Delete a product by ID
     * @param id the product ID
     */
    public void deleteProduct(Long id) {
        deleteById(id);
    }
}
