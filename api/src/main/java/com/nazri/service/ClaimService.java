package com.nazri.service;

import com.nazri.model.Claim;
import com.nazri.model.User;
import com.nazri.model.Warranty;
import com.nazri.repository.ClaimRepository;
import com.nazri.repository.WarrantyRepository;
import com.nazri.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@ApplicationScoped
public class ClaimService {

    @Inject
    ClaimRepository claimRepository;

    @Inject
    WarrantyRepository warrantyRepository;

    // Valid claim statuses
    private static final Set<String> VALID_STATUSES = new HashSet<>();
    static {
        VALID_STATUSES.add("SUBMITTED");
        VALID_STATUSES.add("PROCESSING");
        VALID_STATUSES.add("APPROVED");
        VALID_STATUSES.add("DENIED");
        VALID_STATUSES.add("COMPLETED");
    }

    /**
     * Find a claim by its ID
     * @param id the claim ID
     * @return Optional containing the claim if found
     */
    public Optional<Claim> findById(Long id) {
        return claimRepository.findByIdOptional(id);
    }

    /**
     * Find all claims for a specific user
     * @param userId the user ID
     * @return list of claims
     */
    public List<Claim> findByUserId(Long userId) {
        // Get all warranties for the user first
        List<Warranty> userWarranties = warrantyRepository.findByUserId(userId);
        // Extract warranty IDs
        List<Long> warrantyIds = userWarranties.stream()
                .map(warranty -> warranty.id)
                .toList();
        // Get claims for those warranties
        return claimRepository.findByWarrantyIds(warrantyIds);
    }

    /**
     * Find all claims for a specific warranty and user
     * @param warrantyId the warranty ID
     * @param userId the user ID
     * @return list of claims
     */
    public List<Claim> findByWarrantyIdAndUserId(Long warrantyId, Long userId) {
        // Check if warranty belongs to the user
        Optional<Warranty> warranty = warrantyRepository.findByIdOptional(warrantyId);
        if (warranty.isPresent() && warranty.get().getUser().id.equals(userId)) {
            return claimRepository.findByWarrantyId(warrantyId);
        }
        return List.of(); // Return empty list if warranty doesn't belong to user
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
     * Find claims by user ID and status
     * @param userId the user ID
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByUserIdAndStatus(Long userId, String status) {
        // Get all warranties for the user first
        List<Warranty> userWarranties = warrantyRepository.findByUserId(userId);
        // Extract warranty IDs
        List<Long> warrantyIds = userWarranties.stream()
                .map(warranty -> warranty.id)
                .toList();
        // Get claims for those warranties with the specified status
        return claimRepository.findByWarrantyIdsAndStatus(warrantyIds, status);
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
     * @param user the current user
     * @return the created claim
     */
    @Transactional
    public Claim createClaim(Claim claim, User user) {
        // Validate required fields
        if (claim.getWarranty() == null || claim.getWarranty().id == null) {
            throw new IllegalArgumentException("Warranty is required");
        }

        // Validate warranty exists and belongs to user
        Optional<Warranty> warranty = warrantyRepository.findByIdOptional(claim.getWarranty().id);
        if (warranty.isEmpty()) {
            throw new IllegalArgumentException("Warranty not found");
        }
        
        if (!warranty.get().getUser().id.equals(user.id)) {
            throw new IllegalArgumentException("Warranty does not belong to user");
        }
        
        claim.setWarranty(warranty.get());

        // Validate status
        if (claim.getStatus() != null && !VALID_STATUSES.contains(claim.getStatus().toUpperCase())) {
            throw new IllegalArgumentException("Invalid status. Valid statuses are: " + VALID_STATUSES);
        }

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
     * @param id the claim ID
     * @param claim the claim to update
     * @param user the current user
     * @return the updated claim
     */
    @Transactional
    public Claim updateClaim(Long id, Claim claim, User user) {
        // Validate warranty exists and belongs to user if being updated
        if (claim.getWarranty() != null && claim.getWarranty().id != null) {
            Optional<Warranty> warranty = warrantyRepository.findByIdOptional(claim.getWarranty().id);
            if (warranty.isEmpty()) {
                throw new IllegalArgumentException("Warranty not found");
            }
            
            if (!warranty.get().getUser().id.equals(user.id)) {
                throw new IllegalArgumentException("Warranty does not belong to user");
            }
            
            claim.setWarranty(warranty.get());
        }

        // Validate status if being updated
        if (claim.getStatus() != null && !VALID_STATUSES.contains(claim.getStatus().toUpperCase())) {
            throw new IllegalArgumentException("Invalid status. Valid statuses are: " + VALID_STATUSES);
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
