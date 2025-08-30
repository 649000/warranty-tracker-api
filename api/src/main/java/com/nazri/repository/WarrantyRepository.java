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
    public Optional<Warranty> findById(Long id) {
        return findByIdOptional(id);
    }
    
    /**
     * Find all warranties for a specific user
     * @param userId the user ID
     * @return list of warranties
     */
    public List<Warranty> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }
    
    /**
     * Find warranties by company
     * @param companyId the company ID
     * @return list of warranties
     */
    public List<Warranty> findByCompanyId(Long companyId) {
        return find("company.id", companyId).list();
    }
    
    /**
     * Find warranties by status
     * @param status the warranty status
     * @return list of warranties
     */
    public List<Warranty> findByStatus(String status) {
        return find("status", status).list();
    }
    
    /**
     * Find warranties expiring within a date range
     * @param startDate start date
     * @param endDate end date
     * @return list of warranties
     */
    public List<Warranty> findExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return find("endDate >= ?1 and endDate <= ?2", startDate, endDate).list();
    }
    
    /**
     * Find expired warranties
     * @return list of expired warranties
     */
    public List<Warranty> findExpiredWarranties() {
        return find("endDate < ?1 and status = ?2", LocalDate.now(), "ACTIVE").list();
    }
    
    /**
     * Find warranties by product name (partial match)
     * @param productName the product name to search for
     * @return list of warranties
     */
    public List<Warranty> findByProductNameContaining(String productName) {
        return find("productName like ?1", "%" + productName + "%").list();
    }
    
    /**
     * Find warranties by brand (partial match)
     * @param brand the brand to search for
     * @return list of warranties
     */
    public List<Warranty> findByBrandContaining(String brand) {
        return find("brand like ?1", "%" + brand + "%").list();
    }
    
    /**
     * Find warranties by model number (partial match)
     * @param modelNumber the model number to search for
     * @return list of warranties
     */
    public List<Warranty> findByModelNumberContaining(String modelNumber) {
        return find("modelNumber like ?1", "%" + modelNumber + "%").list();
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
     * Delete a warranty by ID
     * @param id the warranty ID
     */
    public void deleteWarranty(Long id) {
        deleteById(id);
    }
}
