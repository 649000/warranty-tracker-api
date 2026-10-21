package com.nazri.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    void testGettersAndSetters() {
        Product product = new Product();
        product.id = 5L;
        product.setName("Smartphone X");
        product.setBrand("TechBrand");
        product.setModelNumber("TB-X100");

        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        product.setUserProducts(new ArrayList<>());

        assertEquals(5L, product.id);
        assertEquals("Smartphone X", product.getName());
        assertEquals("TechBrand", product.getBrand());
        assertEquals("TB-X100", product.getModelNumber());
        assertEquals(now, product.getCreatedAt());
        assertEquals(now, product.getUpdatedAt());
        assertNotNull(product.getUserProducts());
        assertTrue(product.toString().contains("Smartphone X"));
    }
}
