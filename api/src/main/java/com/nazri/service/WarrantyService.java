package com.nazri.service;

import com.nazri.model.Warranty;
import com.nazri.model.User;
import com.nazri.model.Company;
import com.nazri.model.UserProduct;
import com.nazri.repository.WarrantyRepository;
import com.nazri.repository.UserRepository;
import com.nazri.repository.CompanyRepository;
import com.nazri.repository.UserProductRepository;
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
    UserProductRepository userProductRepository;

    /**
     * Find a warranty by its ID with ownership verification at query level
     * @param userId the user ID
     * @param id the warranty ID
     * @return Optional containing the warranty if found and owned by the user
     */
    public Optional<Warranty> findById(Long userId, Long id) {
        return warrantyRepository.findByIdAndUserId(id, userId);
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
    public List<Warranty> findByStatus(Warranty.WarrantyStatus status) {
        return warrantyRepository.findByStatus(status);
    }

    /**
     * Find warranties by user ID and status
     * @param userId the user ID
     * @param status the warranty status
     * @return list of warranties
     */
    public List<Warranty> findByUserIdAndStatus(Long userId, Warranty.WarrantyStatus status) {
        return warrantyRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Find warranties expiring within a date range
     * @param userId the user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of warranties
     */
    public List<Warranty> findExpiringWarranties(Long userId, LocalDate startDate, LocalDate endDate) {
        return warrantyRepository.findExpiringWarranties(userId, startDate, endDate);
    }

    /**
     * Find expired warranties
     * @return list of expired warranties
     */
    public List<Warranty> findExpiredWarranties() {
        return warrantyRepository.findExpiredWarranties();
    }

    /**
     * Find all warranties
     * @return list of all warranties
     */
    public List<Warranty> findAllWarranties() {
        return warrantyRepository.findAllWarranties();
    }

    /**
     * Find warranties by user product ID
     * @param userId the user ID
     * @param userProductId the user product ID
     * @return list of warranties
     */
    public List<Warranty> findByUserProductId(Long userId, Long userProductId) {
        return warrantyRepository.findByUserProductId(userId, userProductId);
    }

    /**
     * Create a new warranty
     * @param warranty the warranty to create
     * @return the created warranty
     */
    @Transactional
    public Warranty createWarranty(Warranty warranty) {
        // Validate required fields
        if (warranty.getUser() == null || warranty.getUser().id == null) {
            throw new IllegalArgumentException("User is required");
        }

        if (warranty.getCompany() == null || warranty.getCompany().id == null) {
            throw new IllegalArgumentException("Company is required");
        }

        if (warranty.getUserProduct() == null || warranty.getUserProduct().id == null) {
            throw new IllegalArgumentException("User product is required");
        }

        if (warranty.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (warranty.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }

        // Validate user exists
        Optional<User> user = userRepository.findByIdOptional(warranty.getUser().id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        warranty.setUser(user.get());

        // Validate company exists
        Optional<Company> company = companyRepository.findByIdOptional(warranty.getCompany().id);
        if (company.isEmpty()) {
            throw new IllegalArgumentException("Company not found");
        }
        warranty.setCompany(company.get());

        // Validate user product exists
        Optional<UserProduct> userProduct = userProductRepository.findByIdOptional(warranty.getUserProduct().id);
        if (userProduct.isEmpty()) {
            throw new IllegalArgumentException("User product not found");
        }
        warranty.setUserProduct(userProduct.get());

        // Set default status if not provided
        if (warranty.getStatus() == null) {
            warranty.setStatus(Warranty.WarrantyStatus.ACTIVE);
        }

        // Validate dates
        if (warranty.getEndDate().isBefore(warranty.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        return warrantyRepository.createWarranty(warranty);
    }

    /**
     * Update an existing warranty
     * @param userId the user ID
     * @param warranty the warranty to update
     * @return the updated warranty
     */
    @Transactional
    public Warranty updateWarranty(Long userId, Warranty warranty) {
        // First check if the warranty belongs to the user using query-level filtering
        Optional<Warranty> existingWarranty = findById(userId, warranty.id);
        if (existingWarranty.isEmpty()) {
            throw new IllegalArgumentException("Warranty not found or does not belong to user");
        }

        // Validate required fields if being updated
        if (warranty.getUser() != null && warranty.getUser().id != null) {
            Optional<User> user = userRepository.findByIdOptional(warranty.getUser().id);
            if (user.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }
            warranty.setUser(user.get());
        }

        if (warranty.getCompany() != null && warranty.getCompany().id != null) {
            Optional<Company> company = companyRepository.findByIdOptional(warranty.getCompany().id);
            if (company.isEmpty()) {
                throw new IllegalArgumentException("Company not found");
            }
            warranty.setCompany(company.get());
        }

        if (warranty.getUserProduct() != null && warranty.getUserProduct().id != null) {
            Optional<UserProduct> userProduct = userProductRepository.findByIdOptional(warranty.getUserProduct().id);
            if (userProduct.isEmpty()) {
                throw new IllegalArgumentException("User product not found");
            }
            warranty.setUserProduct(userProduct.get());
        }

        // Validate dates if provided
        if (warranty.getStartDate() != null && warranty.getEndDate() != null) {
            if (warranty.getEndDate().isBefore(warranty.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }

        return warrantyRepository.updateWarranty(warranty);
    }

    /**
     * Delete a warranty by ID
     * @param userId the user ID
     * @param id the warranty ID
     */
    @Transactional
    public void deleteWarranty(Long userId, Long id) {
        warrantyRepository.deleteWarranty(userId, id);
    }

    /**
     * Check if a warranty is expired
     * @param warranty the warranty to check
     * @return true if expired, false otherwise
     */
    public boolean isWarrantyExpired(Warranty warranty) {
        return warrantyRepository.isWarrantyExpired(warranty);
    }
}
