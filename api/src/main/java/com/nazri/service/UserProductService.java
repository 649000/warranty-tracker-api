package com.nazri.service;

import com.nazri.model.UserProduct;
import com.nazri.repository.UserProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserProductService {
    
    @Inject
    UserProductRepository userProductRepository;
    
    /**
     * Find a user product by its ID
     * @param id the user product ID
     * @return Optional containing the user product if found
     */
    public Optional<UserProduct> findById(Long id) {
        return Optional.ofNullable(userProductRepository.findById(id));
    }
    
    /**
     * Find all user products for a specific user
     * @param userId the user ID
     * @return list of user products
     */
    public List<UserProduct> findByUserId(Long userId) {
        return userProductRepository.findByUserId(userId);
    }
    
    /**
     * Find all user products for a specific product
     * @param productId the product ID
     * @return list of user products
     */
    public List<UserProduct> findByProductId(Long productId) {
        return userProductRepository.findByProductId(productId);
    }
    
    /**
     * Find a user product by its serial number
     * @param serialNumber the serial number
     * @return Optional containing the user product if found
     */
    public Optional<UserProduct> findBySerialNumber(String serialNumber) {
        return userProductRepository.findBySerialNumber(serialNumber);
    }
    
    /**
     * Find user products by user ID and product ID
     * @param userId the user ID
     * @param productId the product ID
     * @return list of user products
     */
    public List<UserProduct> findByUserIdAndProductId(Long userId, Long productId) {
        return userProductRepository.findByUserIdAndProductId(userId, productId);
    }
    
    /**
     * Create a new user product
     * @param userProduct the user product to create
     * @return the created user product
     */
    @Transactional
    public UserProduct createUserProduct(UserProduct userProduct) {
        return userProductRepository.createUserProduct(userProduct);
    }
    
    /**
     * Update an existing user product
     * @param userProduct the user product to update
     * @return the updated user product
     */
    @Transactional
    public UserProduct updateUserProduct(UserProduct userProduct) {
        return userProductRepository.updateUserProduct(userProduct);
    }
    
    /**
     * Delete a user product by ID
     * @param id the user product ID
     */
    @Transactional
    public void deleteUserProduct(Long id) {
        userProductRepository.deleteUserProduct(id);
    }
    
    /**
     * Check if a user product exists with the given serial number
     * @param serialNumber the serial number
     * @return true if exists, false otherwise
     */
    public boolean existsBySerialNumber(String serialNumber) {
        return userProductRepository.existsBySerialNumber(serialNumber);
    }
    
    /**
     * Check if a user product exists for a user with the given serial number
     * @param userId the user ID
     * @param serialNumber the serial number
     * @return true if exists, false otherwise
     */
    public boolean existsByUserIdAndSerialNumber(Long userId, String serialNumber) {
        return userProductRepository.existsByUserIdAndSerialNumber(userId, serialNumber);
    }
}
