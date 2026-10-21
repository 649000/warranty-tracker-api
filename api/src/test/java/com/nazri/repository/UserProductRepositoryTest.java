package com.nazri.repository;

import com.nazri.model.UserProduct;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProductRepositoryTest {

    @Test
    void testFindByIdAndUserId() {
        UserProductRepository repo = spy(new UserProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        UserProduct up = new UserProduct();
        when(query.firstResult()).thenReturn(up);
        doReturn(query).when(repo).find(eq("id = ?1 and user.id = ?2"), any(Object[].class));

        UserProduct result = repo.findByIdAndUserId(1L, 2L);
        assertNotNull(result);
    }

    @Test
    void testFindById() {
        UserProductRepository repo = spy(new UserProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        UserProduct up = new UserProduct();
        when(query.firstResult()).thenReturn(up);
        doReturn(query).when(repo).find(eq("id"), any(Object[].class));

        UserProduct result = repo.findById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindByUserId() {
        UserProductRepository repo = spy(new UserProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(new UserProduct()));
        doReturn(query).when(repo).find(eq("user.id"), any(Object[].class));

        List<UserProduct> list = repo.findByUserId(1L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByProductId() {
        UserProductRepository repo = spy(new UserProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(new UserProduct()));
        doReturn(query).when(repo).find(eq("product.id"), any(Object[].class));

        List<UserProduct> list = repo.findByProductId(10L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByUserIdAndProductId() {
        UserProductRepository repo = spy(new UserProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(new UserProduct()));
        doReturn(query).when(repo).find(eq("user.id = ?1 and product.id = ?2"), any(Object[].class));

        List<UserProduct> list = repo.findByUserIdAndProductId(1L, 10L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindBySerialNumber() {
        UserProductRepository repo = spy(new UserProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.firstResultOptional()).thenReturn(Optional.of(new UserProduct()));
        doReturn(query).when(repo).find(eq("serialNumber"), any(Object[].class));

        Optional<UserProduct> opt = repo.findBySerialNumber("SN-1");
        assertTrue(opt.isPresent());
    }

    @Test
    void testFindBySerialNumberAndUserId() {
        UserProductRepository repo = spy(new UserProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.firstResultOptional()).thenReturn(Optional.of(new UserProduct()));
        doReturn(query).when(repo).find(eq("serialNumber = ?1 and user.id = ?2"), any(Object[].class));

        Optional<UserProduct> opt = repo.findBySerialNumberAndUserId("SN-1", 1L);
        assertTrue(opt.isPresent());
    }

    @Test
    void testCreateUserProduct() {
        UserProductRepository repo = spy(new UserProductRepository());
        doNothing().when(repo).persistAndFlush(any());

        UserProduct up = new UserProduct();
        UserProduct created = repo.createUserProduct(up);

        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(repo).persistAndFlush(up);
    }

    @Test
    void testUpdateUserProduct() {
        UserProductRepository repo = spy(new UserProductRepository());
        EntityManager em = mock(EntityManager.class);
        doReturn(em).when(repo).getEntityManager();

        UserProduct up = new UserProduct();
        UserProduct updated = repo.updateUserProduct(up);

        assertNotNull(updated.getUpdatedAt());
        verify(em).merge(up);
    }

    @Test
    void testDeleteUserProduct() {
        UserProductRepository repo = spy(new UserProductRepository());
        doReturn(true).when(repo).deleteById(1L);

        repo.deleteUserProduct(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void testExistsBySerialNumber() {
        UserProductRepository repo = spy(new UserProductRepository());
        doReturn(1L).when(repo).count(eq("serialNumber"), any(Object[].class));
        assertTrue(repo.existsBySerialNumber("SN-1"));
    }

    @Test
    void testExistsByUserIdAndSerialNumber() {
        UserProductRepository repo = spy(new UserProductRepository());
        doReturn(1L).when(repo).count(eq("user.id = ?1 and serialNumber = ?2"), any(Object[].class));
        assertTrue(repo.existsByUserIdAndSerialNumber(1L, "SN-1"));
    }
}
