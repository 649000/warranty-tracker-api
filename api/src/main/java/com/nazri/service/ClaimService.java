package com.nazri.service;

import com.nazri.model.Claim;
import com.nazri.model.User;
import com.nazri.model.Warranty;
import com.nazri.repository.ClaimRepository;
import com.nazri.repository.WarrantyRepository;
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
    private static final Set<Claim.ClaimStatus> VALID_STATUSES = new HashSet<>();
    static {
        VALID_STATUSES.add(Claim.ClaimStatus.SUBMITTED);
        VALID_STATUSES.add(Claim.ClaimStatus.PROCESSING);
        VALID_STATUSES.add(Claim.ClaimStatus.APPROVED);
        VALID_STATUSES.add(Claim.ClaimStatus.DENIED);
        VALID_STATUSES.add(Claim.ClaimStatus.COMPLETED);
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
     * Find a claim by its ID and user ID
     * @param id the claim ID
     * @param userId the user ID
     * @return Optional containing the claim if found
     */
    public Optional<Claim> findByIdAndUserId(Long id, Long userId) {
        return claimRepository.findByIdAndUserId(id, userId);
    }

    /**
     * Find all claims for a specific user
     * @param userId the user ID
     * @return list of claims
     */
    public List<Claim> findByUserId(Long userId) {
        return claimRepository.findByUserId(userId);
    }

    /**
     * Find all claims for a specific warranty and user
     * @param warrantyId the warranty ID
     * @param userId the user ID
     * @return list of claims
     */
    public List<Claim> findByWarrantyIdAndUserId(Long warrantyId, Long userId) {
        return claimRepository.findByWarrantyIdAndUserId(warrantyId, userId);
    }

    /**
     * Find all claims (for admin use)
     * @return list of all claims
     */
    public List<Claim> findAllClaims() {
        return claimRepository.findAllClaims();
    }

    /**
     * Find claims by user ID and status
     * @param userId the user ID
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByUserIdAndStatus(Long userId, Claim.ClaimStatus status) {
        return claimRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Find claims by warranty IDs and status
     * @param warrantyIds list of warranty IDs
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByWarrantyIdsAndStatus(List<Long> warrantyIds, Claim.ClaimStatus status) {
        return claimRepository.findByWarrantyIdsAndStatus(warrantyIds, status);
    }

    /**
     * Find claims by status
     * @param status the claim status
     * @return list of claims
     */
    public List<Claim> findByStatus(Claim.ClaimStatus status) {
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
     * Validate claim data
     * @param claim the claim to validate
     * @param user the current user
     */
    private void validateClaim(Claim claim, User user) {
        // Validate required fields
        if (claim.getWarranty() == null || claim.getWarranty().id == null) {
            throw new IllegalArgumentException("Warranty is required");
        }

        if (claim.getClaimDate() == null) {
            throw new IllegalArgumentException("Claim date is required");
        }

        if (claim.getStatus() == null) {
            throw new IllegalArgumentException("Status is required");
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
        if (!VALID_STATUSES.contains(claim.getStatus())) {
            throw new IllegalArgumentException("Invalid status. Valid statuses are: " + VALID_STATUSES);
        }
    }

    /**
     * Create a new claim
     * @param claim the claim to create
     * @param user the current user
     * @return the created claim
     */
    @Transactional
    public Claim createClaim(Claim claim, User user) {
        validateClaim(claim, user);

        // Set claim date if not provided
        if (claim.getClaimDate() == null) {
            claim.setClaimDate(LocalDateTime.now());
        }

        // Set default status if not provided
        if (claim.getStatus() == null) {
            claim.setStatus(Claim.ClaimStatus.SUBMITTED);
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
        // First, ensure the claim exists and belongs to the user
        Optional<Claim> existingClaim = claimRepository.findByIdAndUserId(id, user.id);
        if (existingClaim.isEmpty()) {
            throw new IllegalArgumentException("Claim not found or does not belong to user");
        }

        // Validate the claim data
        validateClaim(claim, user);

        return claimRepository.updateClaim(claim);
    }

    /**
     * Delete a claim by ID
     * @param id the claim ID
     * @param userId the user ID
     */
    @Transactional
    public void deleteClaim(Long id, Long userId) {
        if (!claimRepository.deleteClaimByIdAndUserId(id, userId)) {
            throw new IllegalArgumentException("Claim not found or does not belong to user");
        }
    }
}
