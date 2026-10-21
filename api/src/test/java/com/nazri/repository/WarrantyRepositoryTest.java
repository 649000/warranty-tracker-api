package com.nazri.repository;

import com.nazri.model.Warranty;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarrantyRepositoryTest {

    @Test
    void testFindByIdOptional() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        Warranty w = new Warranty();
        doReturn(w).when(repo).findById(1L);

        Optional<Warranty> opt = repo.findByIdOptional(1L);
        assertTrue(opt.isPresent());
    }

    @Test
    void testFindByIdAndUserId() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.firstResultOptional()).thenReturn(Optional.of(new Warranty()));
        doReturn(query).when(repo).find(eq("id = ?1 and user.id = ?2"), any(Object[].class));

        Optional<Warranty> opt = repo.findByIdAndUserId(1L, 2L);
        assertTrue(opt.isPresent());
    }

    @Test
    void testFindByUserId() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doReturn(List.of(new Warranty())).when(repo).list(eq("user.id"), any(Object[].class));

        List<Warranty> list = repo.findByUserId(1L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByCompanyId() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doReturn(List.of(new Warranty())).when(repo).list(eq("company.id"), any(Object[].class));

        List<Warranty> list = repo.findByCompanyId(10L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByStatus() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doReturn(List.of(new Warranty())).when(repo).list(eq("status"), any(Object[].class));

        List<Warranty> list = repo.findByStatus(Warranty.WarrantyStatus.ACTIVE);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByUserIdAndStatus() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doReturn(List.of(new Warranty())).when(repo).list(eq("user.id = ?1 and status = ?2"), any(Object[].class));

        List<Warranty> list = repo.findByUserIdAndStatus(1L, Warranty.WarrantyStatus.ACTIVE);
        assertEquals(1, list.size());
    }

    @Test
    void testFindExpiringWarranties() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doReturn(List.of(new Warranty())).when(repo).list(anyString(), any(Object[].class));

        List<Warranty> list = repo.findExpiringWarranties(1L, LocalDate.now(), LocalDate.now().plusDays(30));
        assertEquals(1, list.size());
    }

    @Test
    void testFindExpiredWarranties() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doReturn(List.of(new Warranty())).when(repo).list(anyString(), any(Object[].class));

        List<Warranty> list = repo.findExpiredWarranties();
        assertEquals(1, list.size());
    }

    @Test
    void testFindAllWarranties() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(new Warranty()));
        doReturn(query).when(repo).findAll();

        List<Warranty> list = repo.findAllWarranties();
        assertEquals(1, list.size());
    }

    @Test
    void testFindByUserProductId() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doReturn(List.of(new Warranty())).when(repo).list(eq("user.id = ?1 and userProduct.id = ?2"), any(Object[].class));

        List<Warranty> list = repo.findByUserProductId(1L, 100L);
        assertEquals(1, list.size());
    }

    @Test
    void testIsWarrantyExpired() {
        WarrantyRepository repo = new WarrantyRepository();
        Warranty expired = new Warranty();
        expired.setEndDate(LocalDate.now().minusDays(5));
        assertTrue(repo.isWarrantyExpired(expired));

        Warranty active = new Warranty();
        active.setEndDate(LocalDate.now().plusDays(5));
        assertFalse(repo.isWarrantyExpired(active));
    }

    @Test
    void testCreateWarranty() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doNothing().when(repo).persistAndFlush(any());

        Warranty w = new Warranty();
        Warranty created = repo.createWarranty(w);

        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(repo).persistAndFlush(w);
    }

    @Test
    void testUpdateWarranty() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        EntityManager em = mock(EntityManager.class);
        doReturn(em).when(repo).getEntityManager();

        Warranty w = new Warranty();
        Warranty updated = repo.updateWarranty(w);

        assertNotNull(updated.getUpdatedAt());
        verify(em).merge(w);
    }

    @Test
    void testDeleteWarranty() {
        WarrantyRepository repo = spy(new WarrantyRepository());
        doReturn(1L).when(repo).delete(anyString(), any(Object[].class));

        repo.deleteWarranty(1L, 50L);
        verify(repo).delete(eq("id = ?1 and user.id = ?2"), any(Object[].class));
    }
}
