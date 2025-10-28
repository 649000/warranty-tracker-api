package com.nazri.repository;

import com.nazri.model.Warranty;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WarrantyRepository implements PanacheRepository<Warranty> {

    /**
     * Find a warranty by its ID
     * @param id the warranty ID
     * @return Optional containing the warranty if found
     */
    public Optional<Warranty> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }

    /**
     * Find a warranty by its ID and user ID (ownership check at query level)
     * @param id the warranty ID
     * @param userId the user ID
     * @return Optional containing the warranty if found and owned by the user
     */
    public Optional<Warranty> findByIdAndUserId(Long id, Long userId) {
        return find("id = ?1 and user.id = ?2", id, userId).firstResultOptional();
    }

    /**
     * Find all warranties for a specific user
     * @param userId the user ID
     * @return list of warranties
     */
    public List<Warranty> findByUserId(Long userId) {
        return list("user.id", userId);
    }

    /**
     * Find warranties by company
     * @param companyId the company ID
     * @return list of warranties
     */
    public List<Warranty> findByCompanyId(Long companyId) {
        return list("company.id", companyId);
    }

    /**
     * Find warranties by status
     * @param status the warranty status
     * @return list of warranties
     */
    public List<Warranty> findByStatus(Warranty.WarrantyStatus status) {
        return list("status", status);
    }

    /**
     * Find warranties by user ID and status
     * @param userId the user ID
     * @param status the warranty status
     * @return list of warranties
     */
    public List<Warranty> findByUserIdAndStatus(Long userId, Warranty.WarrantyStatus status) {
        return list("user.id = ?1 and status = ?2", userId, status);
    }

    /**
     * Find warranties expiring within a date range for a specific user
     * @param userId the user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of warranties
     */
    public List<Warranty> findExpiringWarranties(Long userId, LocalDate startDate, LocalDate endDate) {
        return list("user.id = ?1 and endDate >= ?2 and endDate <= ?3 and status = ?4", 
                   userId, startDate, endDate, Warranty.WarrantyStatus.ACTIVE);
    }

    /**
     * Find expired warranties
     * @return list of expired warranties
     */
    public List<Warranty> findExpiredWarranties() {
        return list("endDate < ?1 and status = ?2", LocalDate.now(), Warranty.WarrantyStatus.ACTIVE);
    }

    /**
     * Find all warranties
     * @return list of all warranties
     */
    public List<Warranty> findAllWarranties() {
        return findAll().list();
    }

    /**
     * Find warranties by user product ID for a specific user
     * @param userId the user ID
     * @param userProductId the user product ID
     * @return list of warranties
     */
    public List<Warranty> findByUserProductId(Long userId, Long userProductId) {
        return list("user.id = ?1 and userProduct.id = ?2", userId, userProductId);
    }

    /**
     * Check if a warranty is expired
     * @param warranty the warranty to check
     * @return true if expired, false otherwise
     */
    public boolean isWarrantyExpired(Warranty warranty) {
        return warranty.getEndDate().isBefore(LocalDate.now());
    }

    /**
     * Create a new warranty
     * @param warranty the warranty to create
     * @return the created warranty
     */
    public Warranty createWarranty(Warranty warranty) {
        warranty.setCreatedAt(LocalDateTime.now());
        warranty.setUpdatedAt(LocalDateTime.now());
        persistAndFlush(warranty);
        return warranty;
    }

    /**
     * Update an existing warranty
     * @param warranty the warranty to update
     * @return the updated warranty
     */
    public Warranty updateWarranty(Warranty warranty) {
        warranty.setUpdatedAt(LocalDateTime.now());
        getEntityManager().merge(warranty);
        return warranty;
    }

    /**
     * Delete a warranty by ID for a specific user
     * @param userId the user ID
     * @param id the warranty ID
     */
    public void deleteWarranty(Long userId, Long id) {
        delete("id = ?1 and user.id = ?2", id, userId);
    }
}
