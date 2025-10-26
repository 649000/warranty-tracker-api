package com.nazri.repository;

import com.nazri.model.UserProduct;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserProductRepository implements PanacheRepository<UserProduct> {
    
    /**
     * Find a user product by its ID and user ID
     * @param id the user product ID
     * @param userId the user ID
     * @return the user product if found, null otherwise
     */
    public UserProduct findByIdAndUserId(Long id, Long userId) {
        return find("id = ?1 and user.id = ?2", id, userId).firstResult();
    }
    
    /**
     * Find a user product by its ID
     * @param id the user product ID
     * @return the user product if found, null otherwise
     */
    public UserProduct findById(Long id) {
        return find("id", id).firstResult();
    }
    
    public List<UserProduct> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }
    
    public List<UserProduct> findByProductId(Long productId) {
        return find("product.id", productId).list();
    }
    
    public List<UserProduct> findByUserIdAndProductId(Long userId, Long productId) {
        return find("user.id = ?1 and product.id = ?2", userId, productId).list();
    }
    
    public Optional<UserProduct> findBySerialNumber(String serialNumber) {
        return find("serialNumber", serialNumber).firstResultOptional();
    }
    
    public Optional<UserProduct> findBySerialNumberAndUserId(String serialNumber, Long userId) {
        return find("serialNumber = ?1 and user.id = ?2", serialNumber, userId).firstResultOptional();
    }
    
    public UserProduct createUserProduct(UserProduct userProduct) {
        userProduct.setCreatedAt(LocalDateTime.now());
        userProduct.setUpdatedAt(LocalDateTime.now());
        persistAndFlush(userProduct);
        return userProduct;
    }
    
    public UserProduct updateUserProduct(UserProduct userProduct) {
        userProduct.setUpdatedAt(LocalDateTime.now());
        getEntityManager().merge(userProduct);
        return userProduct;
    }
    
    public void deleteUserProduct(Long id) {
        deleteById(id);
    }
    
    public boolean existsBySerialNumber(String serialNumber) {
        return count("serialNumber", serialNumber) > 0;
    }
    
    public boolean existsByUserIdAndSerialNumber(Long userId, String serialNumber) {
        return count("user.id = ?1 and serialNumber = ?2", userId, serialNumber) > 0;
    }
}
