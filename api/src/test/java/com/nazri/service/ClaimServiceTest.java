package com.nazri.service;

import com.nazri.model.Claim;
import com.nazri.model.User;
import com.nazri.model.Warranty;
import com.nazri.repository.ClaimRepository;
import com.nazri.repository.WarrantyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

    @Mock
    ClaimRepository claimRepository;

    @Mock
    WarrantyRepository warrantyRepository;

    @InjectMocks
    ClaimService claimService;

    private User sampleUser;
    private Warranty sampleWarranty;
    private Claim sampleClaim;

    @BeforeEach
    void setUp() {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        sampleWarranty = new Warranty();
        sampleWarranty.id = 10L;
        sampleWarranty.setUser(sampleUser);

        sampleClaim = new Claim();
        sampleClaim.id = 100L;
        sampleClaim.setWarranty(sampleWarranty);
        sampleClaim.setClaimDate(LocalDateTime.now());
        sampleClaim.setStatus(Claim.ClaimStatus.SUBMITTED);
        sampleClaim.setIssueDescription("Broken screen");
    }

    @Test
    void testFindById() {
        when(claimRepository.findByIdOptional(100L)).thenReturn(Optional.of(sampleClaim));

        Optional<Claim> result = claimService.findById(100L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().id);
    }

    @Test
    void testFindByIdAndUserId() {
        when(claimRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleClaim));

        Optional<Claim> result = claimService.findByIdAndUserId(100L, 1L);

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUserId() {
        when(claimRepository.findByUserId(1L)).thenReturn(List.of(sampleClaim));

        List<Claim> result = claimService.findByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByWarrantyIdAndUserId() {
        when(claimRepository.findByWarrantyIdAndUserId(10L, 1L)).thenReturn(List.of(sampleClaim));

        List<Claim> result = claimService.findByWarrantyIdAndUserId(10L, 1L);

        assertEquals(1, result.size());
    }

    @Test
    void testFindAllClaims() {
        when(claimRepository.findAllClaims()).thenReturn(List.of(sampleClaim));

        List<Claim> result = claimService.findAllClaims();

        assertEquals(1, result.size());
    }

    @Test
    void testFindByUserIdAndStatus() {
        when(claimRepository.findByUserIdAndStatus(1L, Claim.ClaimStatus.SUBMITTED)).thenReturn(List.of(sampleClaim));

        List<Claim> result = claimService.findByUserIdAndStatus(1L, Claim.ClaimStatus.SUBMITTED);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByWarrantyIdsAndStatus() {
        when(claimRepository.findByWarrantyIdsAndStatus(List.of(10L), Claim.ClaimStatus.SUBMITTED)).thenReturn(List.of(sampleClaim));

        List<Claim> result = claimService.findByWarrantyIdsAndStatus(List.of(10L), Claim.ClaimStatus.SUBMITTED);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByStatus() {
        when(claimRepository.findByStatus(Claim.ClaimStatus.APPROVED)).thenReturn(List.of(sampleClaim));

        List<Claim> result = claimService.findByStatus(Claim.ClaimStatus.APPROVED);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        when(claimRepository.findByDateRange(start, end)).thenReturn(List.of(sampleClaim));

        List<Claim> result = claimService.findByDateRange(start, end);

        assertEquals(1, result.size());
    }

    @Test
    void testCreateClaim_Success() {
        when(warrantyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleWarranty));
        when(claimRepository.createClaim(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim created = claimService.createClaim(sampleClaim, sampleUser);

        assertNotNull(created);
        assertEquals(Claim.ClaimStatus.SUBMITTED, created.getStatus());
        verify(claimRepository).createClaim(sampleClaim);
    }

    @Test
    void testCreateClaim_ValidationFailures() {
        Claim c = new Claim();

        // Missing warranty
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> claimService.createClaim(c, sampleUser));
        assertEquals("Warranty is required", ex.getMessage());

        c.setWarranty(sampleWarranty);
        // Missing claim date
        ex = assertThrows(IllegalArgumentException.class, () -> claimService.createClaim(c, sampleUser));
        assertEquals("Claim date is required", ex.getMessage());

        c.setClaimDate(LocalDateTime.now());
        // Missing status
        ex = assertThrows(IllegalArgumentException.class, () -> claimService.createClaim(c, sampleUser));
        assertEquals("Status is required", ex.getMessage());
    }

    @Test
    void testCreateClaim_WarrantyNotFound() {
        when(warrantyRepository.findByIdOptional(10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> claimService.createClaim(sampleClaim, sampleUser));
        assertEquals("Warranty not found", ex.getMessage());
    }

    @Test
    void testCreateClaim_WarrantyNotBelongToUser() {
        User anotherUser = new User("uid-2", "another@example.com");
        anotherUser.id = 2L;
        Warranty otherWarranty = new Warranty();
        otherWarranty.id = 10L;
        otherWarranty.setUser(anotherUser);

        when(warrantyRepository.findByIdOptional(10L)).thenReturn(Optional.of(otherWarranty));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> claimService.createClaim(sampleClaim, sampleUser));
        assertEquals("Warranty does not belong to user", ex.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCreateClaim_InvalidStatus() throws Exception {
        when(warrantyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleWarranty));

        java.lang.reflect.Field field = ClaimService.class.getDeclaredField("VALID_STATUSES");
        field.setAccessible(true);
        java.util.Set<Claim.ClaimStatus> set = (java.util.Set<Claim.ClaimStatus>) field.get(null);

        set.remove(Claim.ClaimStatus.SUBMITTED);
        try {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    claimService.createClaim(sampleClaim, sampleUser));
            assertTrue(ex.getMessage().contains("Invalid status"));
        } finally {
            set.add(Claim.ClaimStatus.SUBMITTED);
        }
    }

    @Test
    void testCreateClaim_DefaultDateAndStatus() {
        ClaimService spyService = spy(claimService);
        doNothing().when(spyService).validateClaim(any(Claim.class), any(User.class));
        when(claimRepository.createClaim(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim bareClaim = new Claim();
        bareClaim.setWarranty(sampleWarranty);

        Claim created = spyService.createClaim(bareClaim, sampleUser);

        assertNotNull(created);
        assertNotNull(created.getClaimDate());
        assertEquals(Claim.ClaimStatus.SUBMITTED, created.getStatus());
    }

    @Test
    void testUpdateClaim_Success() {
        when(claimRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleClaim));
        when(warrantyRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleWarranty));
        when(claimRepository.updateClaim(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim updated = claimService.updateClaim(100L, sampleClaim, sampleUser);

        assertNotNull(updated);
        verify(claimRepository).updateClaim(sampleClaim);
    }

    @Test
    void testUpdateClaim_NotFound() {
        when(claimRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> claimService.updateClaim(100L, sampleClaim, sampleUser));
        assertEquals("Claim not found or does not belong to user", ex.getMessage());
    }

    @Test
    void testDeleteClaim_Success() {
        when(claimRepository.deleteClaimByIdAndUserId(100L, 1L)).thenReturn(true);

        assertDoesNotThrow(() -> claimService.deleteClaim(100L, 1L));
        verify(claimRepository).deleteClaimByIdAndUserId(100L, 1L);
    }

    @Test
    void testDeleteClaim_NotFoundThrowsException() {
        when(claimRepository.deleteClaimByIdAndUserId(100L, 1L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> claimService.deleteClaim(100L, 1L));
        assertEquals("Claim not found or does not belong to user", ex.getMessage());
    }
}
