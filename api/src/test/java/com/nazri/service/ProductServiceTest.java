package com.nazri.service;

import com.nazri.model.Product;
import com.nazri.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.id = 1L;
        sampleProduct.setName("MacBook Pro");
        sampleProduct.setBrand("Apple");
        sampleProduct.setModelNumber("MBP-M3");
    }

    @Test
    void testFindAllProducts() {
        when(productRepository.listAll()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.findAllProducts();

        assertEquals(1, result.size());
        verify(productRepository).listAll();
    }

    @Test
    void testFindById_Found() {
        when(productRepository.findById(1L)).thenReturn(sampleProduct);

        Optional<Product> result = productService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("MacBook Pro", result.get().getName());
    }

    @Test
    void testFindById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(null);

        Optional<Product> result = productService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByModelNumber_Found() {
        when(productRepository.findByModelNumber("MBP-M3")).thenReturn(List.of(sampleProduct));

        Optional<Product> result = productService.findByModelNumber("MBP-M3");

        assertTrue(result.isPresent());
        assertEquals(sampleProduct, result.get());
    }

    @Test
    void testFindByModelNumber_NotFound() {
        when(productRepository.findByModelNumber("UNKNOWN")).thenReturn(Collections.emptyList());

        Optional<Product> result = productService.findByModelNumber("UNKNOWN");

        assertFalse(result.isPresent());
    }

    @Test
    void testSearchProducts_AllNullOrBlankReturnsAll() {
        when(productRepository.listAll()).thenReturn(List.of(sampleProduct));

        List<Product> result1 = productService.searchProducts(null, null, null);
        List<Product> result2 = productService.searchProducts("  ", "", null);

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        verify(productRepository, times(2)).listAll();
    }

    @Test
    void testSearchProducts_WithCriteriaDelegatesToRepo() {
        when(productRepository.searchProducts("Mac", "Apple", "MBP")).thenReturn(List.of(sampleProduct));
        when(productRepository.searchProducts("  ", "  ", "MBP")).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.searchProducts("Mac", "Apple", "MBP");
        assertEquals(1, result.size());

        List<Product> result2 = productService.searchProducts("  ", "  ", "MBP");
        assertEquals(1, result2.size());
    }

    @Test
    void testCreateProduct_Success() {
        Product toCreate = new Product();
        toCreate.setName("  iPad Air  ");
        toCreate.setBrand("  Apple  ");
        toCreate.setModelNumber("  IPA-M2  ");

        when(productRepository.findByModelNumber("IPA-M2")).thenReturn(Collections.emptyList());
        when(productRepository.createProduct(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product created = productService.createProduct(toCreate);

        assertEquals("iPad Air", created.getName());
        assertEquals("Apple", created.getBrand());
        assertEquals("IPA-M2", created.getModelNumber());
        verify(productRepository).createProduct(toCreate);
    }

    @Test
    void testCreateProduct_MissingNameThrowsException() {
        Product toCreate = new Product();
        toCreate.setName("   ");
        toCreate.setModelNumber("NUM-123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(toCreate);
        });

        assertEquals("Product name is required", ex.getMessage());

        toCreate.setName(null);
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(toCreate));
    }

    @Test
    void testCreateProduct_MissingModelNumberThrowsException() {
        Product toCreate = new Product();
        toCreate.setName("Valid Name");
        toCreate.setModelNumber("  ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(toCreate);
        });

        assertEquals("Model number is required", ex.getMessage());

        toCreate.setModelNumber(null);
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(toCreate));
    }

    @Test
    void testCreateProduct_DuplicateModelNumberThrowsException() {
        Product toCreate = new Product();
        toCreate.setName("Duplicate Model Product");
        toCreate.setModelNumber("MBP-M3");

        when(productRepository.findByModelNumber("MBP-M3")).thenReturn(List.of(sampleProduct));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(toCreate);
        });

        assertEquals("Product with this model number already exists", ex.getMessage());
        verify(productRepository, never()).createProduct(any());
    }

    @Test
    void testUpdateProduct_Success() {
        Product toUpdate = new Product();
        toUpdate.setName("  MacBook Pro Updated  ");
        toUpdate.setModelNumber("  MBP-M3-MAX  ");
        toUpdate.setBrand("  Apple Inc  ");

        when(productRepository.updateProduct(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product updated = productService.updateProduct(toUpdate);

        assertEquals("MacBook Pro Updated", updated.getName());
        assertEquals("MBP-M3-MAX", updated.getModelNumber());
        assertEquals("Apple Inc", updated.getBrand());
    }

    @Test
    void testCreateProduct_NullBrand() {
        Product toCreate = new Product();
        toCreate.setName("No Brand Product");
        toCreate.setModelNumber("NBP-1");
        toCreate.setBrand(null);

        when(productRepository.findByModelNumber("NBP-1")).thenReturn(Collections.emptyList());
        when(productRepository.createProduct(any())).thenAnswer(inv -> inv.getArgument(0));

        Product created = productService.createProduct(toCreate);
        assertNull(created.getBrand());
    }

    @Test
    void testUpdateProduct_NullBrand() {
        Product toUpdate = new Product();
        toUpdate.setName("No Brand");
        toUpdate.setModelNumber("NBP-1");
        toUpdate.setBrand(null);

        when(productRepository.updateProduct(any())).thenAnswer(inv -> inv.getArgument(0));

        Product updated = productService.updateProduct(toUpdate);
        assertNull(updated.getBrand());
    }

    @Test
    void testUpdateProduct_ValidationFailures() {
        Product noName = new Product();
        noName.setModelNumber("NUM-1");
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(noName));

        noName.setName("   ");
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(noName));

        Product noModel = new Product();
        noModel.setName("Valid Name");
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(noModel));

        noModel.setModelNumber("   ");
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(noModel));
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteProduct(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteProduct(1L);
    }
}
