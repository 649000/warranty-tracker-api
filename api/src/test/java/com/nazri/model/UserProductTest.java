package com.nazri.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserProductTest {

    @Test
    void testGettersAndSetters() {
        UserProduct up = new UserProduct();
        up.id = 15L;
        up.setSerialNumber("SN-987654321");
        LocalDate purchaseDate = LocalDate.of(2025, 3, 15);
        up.setPurchaseDate(purchaseDate);
        up.setPurchasePrice(new BigDecimal("299.99"));
        up.setPurchaseLocation("BestBuy");
        up.setReceiptNumber("REC-12345");
        up.setNotes("Gift from parents");

        LocalDateTime now = LocalDateTime.now();
        up.setCreatedAt(now);
        up.setUpdatedAt(now);

        Product product = new Product();
        product.id = 2L;
        up.setProduct(product);

        User user = new User();
        user.id = 1L;
        up.setUser(user);

        up.setWarranties(new ArrayList<>());
        Receipt receipt = new Receipt();
        up.setReceipt(receipt);

        assertEquals(15L, up.id);
        assertEquals("SN-987654321", up.getSerialNumber());
        assertEquals(purchaseDate, up.getPurchaseDate());
        assertEquals(new BigDecimal("299.99"), up.getPurchasePrice());
        assertEquals("BestBuy", up.getPurchaseLocation());
        assertEquals("REC-12345", up.getReceiptNumber());
        assertEquals("Gift from parents", up.getNotes());
        assertEquals(now, up.getCreatedAt());
        assertEquals(now, up.getUpdatedAt());
        assertEquals(product, up.getProduct());
        assertEquals(user, up.getUser());
        assertNotNull(up.getWarranties());
        assertEquals(receipt, up.getReceipt());
        assertTrue(up.toString().contains("SN-987654321"));
    }
}
