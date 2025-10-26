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
     * Search products by multiple criteria
     * @param name the product name to search for (partial match)
     * @param brand the brand to search for (partial match)
     * @param modelNumber the model number to search for (partial match)
     * @return list of products matching all provided criteria
     */
    public List<Product> searchProducts(String name, String brand, String modelNumber) {
        StringBuilder query = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();
        int paramIndex = 1;
        
        if (name != null && !name.trim().isEmpty()) {
            whereClause.append(" and name like ?").append(paramIndex++);
        }
        
        if (brand != null && !brand.trim().isEmpty()) {
            whereClause.append(" and brand like ?").append(paramIndex++);
        }
        
        if (modelNumber != null && !modelNumber.trim().isEmpty()) {
            whereClause.append(" and modelNumber like ?").append(paramIndex++);
        }
        
        if (whereClause.length() > 0) {
            // Remove the first " and " 
            query.append("FROM Product WHERE ").append(whereClause.substring(5));
            return find(query.toString(), getParameters(name, brand, modelNumber)).list();
        }
        
        return findAll().list();
    }
    
    private Object[] getParameters(String name, String brand, String modelNumber) {
        return new Object[] {
            name != null && !name.trim().isEmpty() ? "%" + name.trim() + "%" : null,
            brand != null && !brand.trim().isEmpty() ? "%" + brand.trim() + "%" : null,
            modelNumber != null && !modelNumber.trim().isEmpty() ? "%" + modelNumber.trim() + "%" : null
        };
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
