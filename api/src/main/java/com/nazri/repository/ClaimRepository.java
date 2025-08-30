package com.nazri.repository;

import com.nazri.model.Claim;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ClaimRepository implements PanacheRepository<Claim> {
    
    /**
     * Find all claims for a specific warranty
     * @param warrantyId the warranty ID
     * @return list of claims
     */
    public List<Claim> findByWarrantyId(Long warrantyId) {
        return find("warranty.id", warrantyId).list();
    }
    
    /**
     * Find claims by status
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByStatus(String status) {
        return find("status", status).list();
    }
    
    /**
     * Find claims within a date range
     * @param startDate start date
     * @param endDate end date
     * @return list of claims
     */
    public List<Claim> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return find("claimDate >= ?1 and claimDate <= ?2", startDate, endDate).list();
    }
    
    /**
     * Create a new claim
     * @param claim the claim to create
     * @return the created claim
     */
    public Claim createClaim(Claim claim) {
        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        persistAndFlush(claim);
        return claim;
    }
    
    /**
     * Update an existing claim
     * @param claim the claim to update
     * @return the updated claim
     */
    public Claim updateClaim(Claim claim) {
        claim.setUpdatedAt(LocalDateTime.now());
        getEntityManager().merge(claim);
        return claim;
    }
    
    /**
     * Delete a claim by ID
     * @param id the claim ID
     */
    public void deleteClaim(Long id) {
        deleteById(id);
    }
}
