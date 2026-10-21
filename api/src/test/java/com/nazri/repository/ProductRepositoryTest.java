package com.nazri.repository;

import com.nazri.model.Product;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryTest {

    @Test
    void testFindByModelNumber() {
        ProductRepository repo = spy(new ProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(new Product()));
        doReturn(query).when(repo).find(eq("modelNumber"), any(Object[].class));

        List<Product> result = repo.findByModelNumber("MOD-1");
        assertEquals(1, result.size());
    }

    @Test
    void testSearchProducts_AllParamsProvided() {
        ProductRepository repo = spy(new ProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(new Product()));
        doReturn(query).when(repo).find(anyString(), any(Object[].class));

        List<Product> result = repo.searchProducts("Phone", "Apple", "A100");
        assertEquals(1, result.size());
    }

    @Test
    void testSearchProducts_NoParamsProvided() {
        ProductRepository repo = spy(new ProductRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(new Product()));
        doReturn(query).when(repo).findAll();

        List<Product> result = repo.searchProducts(null, "", "  ");
        assertEquals(1, result.size());
    }

    @Test
    void testCreateProduct() {
        ProductRepository repo = spy(new ProductRepository());
        doNothing().when(repo).persistAndFlush(any());

        Product product = new Product();
        Product created = repo.createProduct(product);

        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(repo).persistAndFlush(product);
    }

    @Test
    void testUpdateProduct() {
        ProductRepository repo = spy(new ProductRepository());
        EntityManager em = mock(EntityManager.class);
        doReturn(em).when(repo).getEntityManager();

        Product product = new Product();
        Product updated = repo.updateProduct(product);

        assertNotNull(updated.getUpdatedAt());
        verify(em).merge(product);
    }

    @Test
    void testDeleteProduct() {
        ProductRepository repo = spy(new ProductRepository());
        doReturn(true).when(repo).deleteById(1L);

        repo.deleteProduct(1L);
        verify(repo).deleteById(1L);
    }
}
