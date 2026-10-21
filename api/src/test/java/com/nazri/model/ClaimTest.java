package com.nazri.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ClaimTest {

    @Test
    void testGettersAndSetters() {
        Claim claim = new Claim();
        claim.id = 50L;
        LocalDateTime claimDate = LocalDateTime.of(2025, 6, 1, 10, 30);
        claim.setClaimDate(claimDate);
        claim.setStatus(Claim.ClaimStatus.SUBMITTED);
        claim.setReferenceNumber("REF-001");
        claim.setIssueDescription("Screen flickering");
        claim.setResolutionDetails("Awaiting technician inspection");

        LocalDateTime now = LocalDateTime.now();
        claim.setCreatedAt(now);
        claim.setUpdatedAt(now);

        Warranty warranty = new Warranty();
        warranty.id = 100L;
        claim.setWarranty(warranty);

        assertEquals(50L, claim.id);
        assertEquals(claimDate, claim.getClaimDate());
        assertEquals(Claim.ClaimStatus.SUBMITTED, claim.getStatus());
        assertEquals("REF-001", claim.getReferenceNumber());
        assertEquals("Screen flickering", claim.getIssueDescription());
        assertEquals("Awaiting technician inspection", claim.getResolutionDetails());
        assertEquals(now, claim.getCreatedAt());
        assertEquals(now, claim.getUpdatedAt());
        assertEquals(warranty, claim.getWarranty());
        assertTrue(claim.toString().contains("Screen flickering"));
    }

    @Test
    void testClaimStatusEnum() {
        assertEquals(5, Claim.ClaimStatus.values().length);
        assertEquals(Claim.ClaimStatus.SUBMITTED, Claim.ClaimStatus.valueOf("SUBMITTED"));
        assertEquals(Claim.ClaimStatus.PROCESSING, Claim.ClaimStatus.valueOf("PROCESSING"));
        assertEquals(Claim.ClaimStatus.APPROVED, Claim.ClaimStatus.valueOf("APPROVED"));
        assertEquals(Claim.ClaimStatus.DENIED, Claim.ClaimStatus.valueOf("DENIED"));
        assertEquals(Claim.ClaimStatus.COMPLETED, Claim.ClaimStatus.valueOf("COMPLETED"));
    }
}
