package com.nazri.service;

import com.nazri.model.Warranty;
import com.nazri.model.User;
import com.nazri.model.Company;
import com.nazri.model.Product;
import com.nazri.repository.WarrantyRepository;
import com.nazri.repository.UserRepository;
import com.nazri.repository.CompanyRepository;
import com.nazri.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WarrantyService {
    
    @Inject
    WarrantyRepository warrantyRepository;
    
    @Inject
    UserRepository userRepository;
    
    @Inject
    CompanyRepository companyRepository;
    
    @Inject
    ProductRepository productRepository;
    
    /**
     * Find a warranty by its ID
     * @param id the warranty ID
     * @return Optional containing the warranty if found
     */
    public Optional<Warranty> findById(Long id) {
        return Optional.ofNullable(warrantyRepository.findById(id));
    }

    /**
     * Find all warranties for a specific user
     * @param userId the user ID
     * @return list of warranties
     */
    public List<Warranty> findByUserId(Long userId) {
        return warrantyRepository.findByUserId(userId);
    }
    
    /**
     * Find warranties by company
     * @param companyId the company ID
     * @return list of warranties
     */
    public List<Warranty> findByCompanyId(Long companyId) {
        return warrantyRepository.findByCompanyId(companyId);
    }
    
    /**
     * Find warranties by status
     * @param status the warranty status
     * @return list of warranties
     */
    public List<Warranty> findByStatus(String status) {
        return warrantyRepository.findByStatus(status);
    }
    
    /**
     * Find warranties expiring within a date range
     * @param startDate start date
     * @param endDate end date
     * @return list of warranties
     */
    public List<Warranty> findExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return warrantyRepository.findExpiringBetween(startDate, endDate);
    }
    
    /**
     * Find expired warranties
     * @return list of expired warranties
     */
    public List<Warranty> findExpiredWarranties() {
        return warrantyRepository.findExpiredWarranties();
    }
    
    /**
     * Find warranties by product ID
     * @param productId the product ID
     * @return list of warranties
     */
    public List<Warranty> findByProductId(Long productId) {
        return warrantyRepository.findByProductId(productId);
    }
    
    /**
     * Create a new warranty
     * @param warranty the warranty to create
     * @return the created warranty
     */
    @Transactional
    public Warranty createWarranty(Warranty warranty) {
        // Validate user exists
        if (warranty.getUser() == null || warranty.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required");
        }
        
        Optional<User> user = userRepository.findByIdOptional(warranty.getUser().getId());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        warranty.setUser(user.get());
        
        // Validate company exists
        if (warranty.getCompany() == null || warranty.getCompany().getId() == null) {
            throw new IllegalArgumentException("Company is required");
        }
        
        Optional<Company> company = companyRepository.findByIdOptional(warranty.getCompany().getId());
        if (company.isEmpty()) {
            throw new IllegalArgumentException("Company not found");
        }
        warranty.setCompany(company.get());
        
        // Validate product exists
        if (warranty.getProduct() == null || warranty.getProduct().getId() == null) {
            throw new IllegalArgumentException("Product is required");
        }
        
        Optional<Product> product = productRepository.findByIdOptional(warranty.getProduct().getId());
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }
        warranty.setProduct(product.get());
        
        // Set default status if not provided
        if (warranty.getStatus() == null || warranty.getStatus().isEmpty()) {
            warranty.setStatus("ACTIVE");
        }
        
        return warrantyRepository.createWarranty(warranty);
    }
    
    /**
     * Update an existing warranty
     * @param warranty the warranty to update
     * @return the updated warranty
     */
    @Transactional
    public Warranty updateWarranty(Warranty warranty) {
        // Validate user exists if being updated
        if (warranty.getUser() != null && warranty.getUser().getId() != null) {
            Optional<User> user = userRepository.findByIdOptional(warranty.getUser().getId());
            if (user.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }
            warranty.setUser(user.get());
        }
        
        // Validate company exists if being updated
        if (warranty.getCompany() != null && warranty.getCompany().getId() != null) {
            Optional<Company> company = companyRepository.findByIdOptional(warranty.getCompany().getId());
            if (company.isEmpty()) {
                throw new IllegalArgumentException("Company not found");
            }
            warranty.setCompany(company.get());
        }
        
        // Validate product exists if being updated
        if (warranty.getProduct() != null && warranty.getProduct().getId() != null) {
            Optional<Product> product = productRepository.findByIdOptional(warranty.getProduct().getId());
            if (product.isEmpty()) {
                throw new IllegalArgumentException("Product not found");
            }
            warranty.setProduct(product.get());
        }
        
        return warrantyRepository.updateWarranty(warranty);
    }
    
    /**
     * Delete a warranty by ID
     * @param id the warranty ID
     */
    @Transactional
    public void deleteWarranty(Long id) {
        warrantyRepository.deleteWarranty(id);
    }
    
    /**
     * Check if a warranty is expired
     * @param warranty the warranty to check
     * @return true if expired, false otherwise
     */
    public boolean isWarrantyExpired(Warranty warranty) {
        return warranty.getEndDate().isBefore(LocalDate.now());
    }
}
