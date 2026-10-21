package com.nazri.repository;

import com.nazri.model.Claim;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimRepositoryTest {

    @Test
    void testFindByIdOptional() {
        ClaimRepository repo = spy(new ClaimRepository());
        Claim claim = new Claim();
        doReturn(claim).when(repo).findById(1L);

        Optional<Claim> opt = repo.findByIdOptional(1L);
        assertTrue(opt.isPresent());
    }

    @Test
    void testFindAllClaims() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).listAll();

        List<Claim> list = repo.findAllClaims();
        assertEquals(1, list.size());
    }

    @Test
    void testFindByWarrantyId() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).list(eq("warranty.id"), any(Object[].class));

        List<Claim> list = repo.findByWarrantyId(10L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByWarrantyIds() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).list(eq("warranty.id in ?1"), any(Object[].class));

        List<Claim> list = repo.findByWarrantyIds(List.of(10L, 20L));
        assertEquals(1, list.size());
    }

    @Test
    void testFindByUserId() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).list(eq("warranty.user.id"), any(Object[].class));

        List<Claim> list = repo.findByUserId(1L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByWarrantyIdAndUserId() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).list(eq("warranty.id = ?1 and warranty.user.id = ?2"), any(Object[].class));

        List<Claim> list = repo.findByWarrantyIdAndUserId(10L, 1L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByWarrantyIdsAndStatus() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).list(eq("warranty.id in ?1 and status = ?2"), any(Object[].class));

        List<Claim> list = repo.findByWarrantyIdsAndStatus(List.of(10L), Claim.ClaimStatus.SUBMITTED);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByUserIdAndStatus() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).list(eq("warranty.user.id = ?1 and status = ?2"), any(Object[].class));

        List<Claim> list = repo.findByUserIdAndStatus(1L, Claim.ClaimStatus.SUBMITTED);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByStatus() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).list(eq("status"), any(Object[].class));

        List<Claim> list = repo.findByStatus(Claim.ClaimStatus.APPROVED);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByDateRange() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(List.of(new Claim())).when(repo).list(eq("claimDate >= ?1 and claimDate <= ?2"), any(Object[].class));

        List<Claim> list = repo.findByDateRange(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        assertEquals(1, list.size());
    }

    @Test
    void testFindByIdAndUserId() {
        ClaimRepository repo = spy(new ClaimRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.firstResult()).thenReturn(new Claim());
        doReturn(query).when(repo).find(eq("id = ?1 and warranty.user.id = ?2"), any(Object[].class));

        Optional<Claim> opt = repo.findByIdAndUserId(1L, 2L);
        assertTrue(opt.isPresent());
    }

    @Test
    void testCreateClaim() {
        ClaimRepository repo = spy(new ClaimRepository());
        doNothing().when(repo).persistAndFlush(any());

        Claim c = new Claim();
        Claim created = repo.createClaim(c);

        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(repo).persistAndFlush(c);
    }

    @Test
    void testUpdateClaim() {
        ClaimRepository repo = spy(new ClaimRepository());
        EntityManager em = mock(EntityManager.class);
        doReturn(em).when(repo).getEntityManager();

        Claim c = new Claim();
        Claim updated = repo.updateClaim(c);

        assertNotNull(updated.getUpdatedAt());
        verify(em).merge(c);
    }

    @Test
    void testDeleteClaim() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(true).when(repo).deleteById(1L);

        repo.deleteClaim(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void testDeleteClaimByIdAndUserId() {
        ClaimRepository repo = spy(new ClaimRepository());
        doReturn(1L).when(repo).delete(anyString(), any(Object[].class));
        assertTrue(repo.deleteClaimByIdAndUserId(1L, 2L));

        doReturn(0L).when(repo).delete(anyString(), any(Object[].class));
        assertFalse(repo.deleteClaimByIdAndUserId(1L, 2L));
    }
}
