package com.nazri.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiptTest {

    @Test
    void testGettersAndSetters() {
        Receipt receipt = new Receipt();
        receipt.id = 60L;
        receipt.setS3Key("dev/1/receipt_123.jpg");
        receipt.setMerchantName("Target");
        receipt.setTotalAmount(new BigDecimal("89.99"));
        LocalDate receiptDate = LocalDate.of(2025, 4, 10);
        receipt.setReceiptDate(receiptDate);
        receipt.setIsConfirmed(true);

        LocalDateTime now = LocalDateTime.now();
        receipt.setCreatedAt(now);
        receipt.setUpdatedAt(now);

        UserProduct userProduct = new UserProduct();
        userProduct.id = 15L;
        receipt.setUserProduct(userProduct);

        assertEquals(60L, receipt.id);
        assertEquals("dev/1/receipt_123.jpg", receipt.getS3Key());
        assertEquals("Target", receipt.getMerchantName());
        assertEquals(new BigDecimal("89.99"), receipt.getTotalAmount());
        assertEquals(receiptDate, receipt.getReceiptDate());
        assertTrue(receipt.getIsConfirmed());
        assertEquals(now, receipt.getCreatedAt());
        assertEquals(now, receipt.getUpdatedAt());
        assertEquals(userProduct, receipt.getUserProduct());
    }

    @Test
    void testDefaultIsConfirmed() {
        Receipt receipt = new Receipt();
        assertFalse(receipt.getIsConfirmed());
    }
}
