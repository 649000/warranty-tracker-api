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
        return list("warranty.id", warrantyId);
    }
    
    /**
     * Find all claims for a list of warranty IDs
     * @param warrantyIds list of warranty IDs
     * @return list of claims
     */
    public List<Claim> findByWarrantyIds(List<Long> warrantyIds) {
        return list("warranty.id in ?1", warrantyIds);
    }
    
    /**
     * Find claims by warranty IDs and status
     * @param warrantyIds list of warranty IDs
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByWarrantyIdsAndStatus(List<Long> warrantyIds, String status) {
        return list("warranty.id in ?1 and status = ?2", warrantyIds, status);
    }
    
    /**
     * Find claims by status
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByStatus(String status) {
        return list("status", status);
    }
    
    /**
     * Find claims within a date range
     * @param startDate start date
     * @param endDate end date
     * @return list of claims
     */
    public List<Claim> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return list("claimDate >= ?1 and claimDate <= ?2", startDate, endDate);
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
