package com.nazri.repository;

import com.nazri.model.Receipt;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceiptRepositoryTest {

    @Test
    void testFindByIdOptional() {
        ReceiptRepository repo = spy(new ReceiptRepository());
        Receipt receipt = new Receipt();
        doReturn(receipt).when(repo).findById(1L);

        Optional<Receipt> opt = repo.findByIdOptional(1L);
        assertTrue(opt.isPresent());
    }

    @Test
    void testFindByIdAndUserId() {
        ReceiptRepository repo = spy(new ReceiptRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.firstResultOptional()).thenReturn(Optional.of(new Receipt()));
        doReturn(query).when(repo).find(eq("id = ?1 and userProduct.user.id = ?2"), any(Object[].class));

        Optional<Receipt> opt = repo.findByIdAndUserId(1L, 2L);
        assertTrue(opt.isPresent());
    }

    @Test
    void testFindByUserId() {
        ReceiptRepository repo = spy(new ReceiptRepository());
        doReturn(List.of(new Receipt())).when(repo).list(eq("userProduct.user.id"), any(Object[].class));

        List<Receipt> list = repo.findByUserId(1L);
        assertEquals(1, list.size());
    }

    @Test
    void testFindByUserIdAndConfirmed() {
        ReceiptRepository repo = spy(new ReceiptRepository());
        doReturn(List.of(new Receipt())).when(repo).list(eq("userProduct.user.id = ?1 and isConfirmed = ?2"), any(Object[].class));

        List<Receipt> list = repo.findByUserIdAndConfirmed(1L, true);
        assertEquals(1, list.size());
    }

    @Test
    void testCreateReceipt() {
        ReceiptRepository repo = spy(new ReceiptRepository());
        doNothing().when(repo).persistAndFlush(any());

        Receipt receipt = new Receipt();
        Receipt created = repo.createReceipt(receipt);

        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(repo).persistAndFlush(receipt);
    }

    @Test
    void testUpdateReceipt() {
        ReceiptRepository repo = spy(new ReceiptRepository());
        EntityManager em = mock(EntityManager.class);
        doReturn(em).when(repo).getEntityManager();

        Receipt receipt = new Receipt();
        Receipt updated = repo.updateReceipt(receipt);

        assertNotNull(updated.getUpdatedAt());
        verify(em).merge(receipt);
    }

    @Test
    void testDeleteReceipt() {
        ReceiptRepository repo = spy(new ReceiptRepository());
        doReturn(true).when(repo).deleteById(1L);

        repo.deleteReceipt(1L);
        verify(repo).deleteById(1L);
    }
}
