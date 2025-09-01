package com.nazri.repository;

import com.nazri.model.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    
    /**
     * Find products by model number (exact match)
     * @param modelNumber the product model number
     * @return list containing the product if found
     */
    public List<Product> findByModelNumber(String modelNumber) {
        return find("modelNumber", modelNumber).list();
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
     * Find products by name and model number
     * @param name the product name
     * @param modelNumber the model number
     * @return list of products
     */
    public List<Product> findByNameAndModelNumber(String name, String modelNumber) {
        return find("name = ?1 and modelNumber = ?2", name, modelNumber).list();
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
    
    /**
     * Find all products
     * @return list of all products
     */
    public List<Product> listAll() {
        return findAll().list();
    }
    
    /**
     * Find a product by ID
     * @param id the product ID
     * @return the product if found, null otherwise
     */
    public Product findById(Long id) {
        return find("id", id).firstResult();
    }
}
