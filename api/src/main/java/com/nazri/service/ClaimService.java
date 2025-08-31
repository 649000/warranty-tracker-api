package com.nazri.service;

import com.nazri.model.Claim;
import com.nazri.model.Warranty;
import com.nazri.repository.ClaimRepository;
import com.nazri.repository.WarrantyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClaimService {
    
    @Inject
    ClaimRepository claimRepository;
    
    @Inject
    WarrantyRepository warrantyRepository;
    
    /**
     * Find a claim by its ID
     * @param id the claim ID
     * @return Optional containing the claim if found
     */
    public Optional<Claim> findById(Long id) {
        return Optional.ofNullable(claimRepository.findById(id));
    }

    /**
     * Find all claims for a specific warranty
     * @param warrantyId the warranty ID
     * @return list of claims
     */
    public List<Claim> findByWarrantyId(Long warrantyId) {
        return claimRepository.findByWarrantyId(warrantyId);
    }
    
    /**
     * Find all claims for a list of warranty IDs
     * @param warrantyIds list of warranty IDs
     * @return list of claims
     */
    public List<Claim> findByWarrantyIds(List<Long> warrantyIds) {
        return claimRepository.findByWarrantyIds(warrantyIds);
    }
    
    /**
     * Find claims by warranty IDs and status
     * @param warrantyIds list of warranty IDs
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByWarrantyIdsAndStatus(List<Long> warrantyIds, String status) {
        return claimRepository.findByWarrantyIdsAndStatus(warrantyIds, status);
    }
    
    /**
     * Find claims by status
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByStatus(String status) {
        return claimRepository.findByStatus(status);
    }
    
    /**
     * Find claims within a date range
     * @param startDate start date
     * @param endDate end date
     * @return list of claims
     */
    public List<Claim> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return claimRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Create a new claim
     * @param claim the claim to create
     * @return the created claim
     */
    @Transactional
    public Claim createClaim(Claim claim) {
        // Validate warranty exists
        if (claim.getWarranty() == null || claim.getWarranty().id == null) {
            throw new IllegalArgumentException("Warranty is required");
        }
        
        Optional<Warranty> warranty = warrantyRepository.findByIdOptional(claim.getWarranty().id);
        if (warranty.isEmpty()) {
            throw new IllegalArgumentException("Warranty not found");
        }
        claim.setWarranty(warranty.get());
        
        // Set default status if not provided
        if (claim.getStatus() == null || claim.getStatus().isEmpty()) {
            claim.setStatus("SUBMITTED");
        }
        
        // Set claim date if not provided
        if (claim.getClaimDate() == null) {
            claim.setClaimDate(LocalDateTime.now());
        }
        
        return claimRepository.createClaim(claim);
    }
    
    /**
     * Update an existing claim
     * @param claim the claim to update
     * @return the updated claim
     */
    @Transactional
    public Claim updateClaim(Claim claim) {
        // Validate warranty exists if being updated
        if (claim.getWarranty() != null && claim.getWarranty().id != null) {
            Optional<Warranty> warranty = warrantyRepository.findByIdOptional(claim.getWarranty().id);
            if (warranty.isEmpty()) {
                throw new IllegalArgumentException("Warranty not found");
            }
            claim.setWarranty(warranty.get());
        }
        
        return claimRepository.updateClaim(claim);
    }
    
    /**
     * Delete a claim by ID
     * @param id the claim ID
     */
    @Transactional
    public void deleteClaim(Long id) {
        claimRepository.deleteClaim(id);
    }
}
