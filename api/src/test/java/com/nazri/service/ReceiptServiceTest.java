package com.nazri.service;

import com.nazri.model.Receipt;
import com.nazri.model.User;
import com.nazri.model.UserProduct;
import com.nazri.repository.ReceiptRepository;
import com.nazri.repository.UserProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceiptServiceTest {

    @Mock
    ReceiptRepository receiptRepository;

    @Mock
    UserProductRepository userProductRepository;

    @Mock
    S3Service s3Service;

    @Mock
    TextractService textractService;

    @InjectMocks
    ReceiptService receiptService;

    private User sampleUser;
    private UserProduct sampleUserProduct;
    private Receipt sampleReceipt;

    @BeforeEach
    void setUp() {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        sampleUserProduct = new UserProduct();
        sampleUserProduct.id = 10L;
        sampleUserProduct.setUser(sampleUser);

        sampleReceipt = new Receipt();
        sampleReceipt.id = 100L;
        sampleReceipt.setUserProduct(sampleUserProduct);
        sampleReceipt.setS3Key("dev/1/receipt.jpg");
        sampleReceipt.setIsConfirmed(false);
    }

    @Test
    void testUploadReceipt_InvalidFileType() {
        byte[] data = new byte[]{1, 2, 3};

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            receiptService.uploadReceipt(data, "application/pdf", 10L, sampleUser);
        });

        assertEquals("Invalid file type. Only JPEG and PNG files are allowed.", ex.getMessage());
    }

    @Test
    void testUploadReceipt_FileTooLarge() {
        byte[] largeData = new byte[11 * 1024 * 1024];

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            receiptService.uploadReceipt(largeData, "image/jpeg", 10L, sampleUser);
        });

        assertEquals("File size exceeds maximum limit of 10MB.", ex.getMessage());
    }

    @Test
    void testUploadReceipt_UserProductNotFound() {
        byte[] data = new byte[]{1, 2, 3};
        when(userProductRepository.findByIdOptional(10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            receiptService.uploadReceipt(data, "image/jpeg", 10L, sampleUser);
        });

        assertEquals("User product not found", ex.getMessage());
    }

    @Test
    void testUploadReceipt_UserProductNotOwnedByUser() {
        byte[] data = new byte[]{1, 2, 3};
        User otherUser = new User("uid-2", "other@example.com");
        otherUser.id = 2L;

        UserProduct otherProduct = new UserProduct();
        otherProduct.id = 10L;
        otherProduct.setUser(otherUser);

        when(userProductRepository.findByIdOptional(10L)).thenReturn(Optional.of(otherProduct));

        SecurityException ex = assertThrows(SecurityException.class, () -> {
            receiptService.uploadReceipt(data, "image/jpeg", 10L, sampleUser);
        });

        assertTrue(ex.getMessage().contains("Access denied"));
    }

    @Test
    void testUploadReceipt_Success() throws Exception {
        byte[] data = new byte[]{1, 2, 3};
        when(userProductRepository.findByIdOptional(10L)).thenReturn(Optional.of(sampleUserProduct));
        when(s3Service.uploadReceipt(data, 1L, "image/jpeg")).thenReturn("dev/1/receipt.jpg");

        when(receiptRepository.createReceipt(any(Receipt.class))).thenAnswer(inv -> {
            Receipt r = inv.getArgument(0);
            r.id = 100L;
            return r;
        });

        Map<String, Object> extractedData = new HashMap<>();
        extractedData.put("merchantName", "Best Buy");
        extractedData.put("totalAmount", new BigDecimal("199.99"));
        extractedData.put("receiptDate", LocalDate.of(2025, 5, 20));

        when(textractService.extractReceiptData(any(), eq("dev/1/receipt.jpg"))).thenReturn(extractedData);
        when(receiptRepository.updateReceipt(any(Receipt.class))).thenAnswer(inv -> inv.getArgument(0));

        ReceiptService.ReceiptData result = receiptService.uploadReceipt(data, "image/jpeg", 10L, sampleUser);

        assertNotNull(result);
        assertEquals(100L, result.id);
        assertEquals("Best Buy", result.merchantName);
        assertEquals(new BigDecimal("199.99"), result.totalAmount);
        assertEquals(LocalDate.of(2025, 5, 20), result.receiptDate);
        assertEquals("dev/1/receipt.jpg", result.s3Key);
        assertFalse(result.isConfirmed);
    }

    @Test
    void testGetReceiptById() {
        when(receiptRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleReceipt));

        Optional<Receipt> result = receiptService.getReceiptById(100L, sampleUser);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().id);
    }

    @Test
    void testGetReceiptsByUser() {
        when(receiptRepository.findByUserId(1L)).thenReturn(List.of(sampleReceipt));

        List<Receipt> result = receiptService.getReceiptsByUser(sampleUser);

        assertEquals(1, result.size());
    }

    @Test
    void testConfirmReceipt_Success() {
        when(receiptRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleReceipt));
        when(receiptRepository.updateReceipt(any(Receipt.class))).thenAnswer(inv -> inv.getArgument(0));

        Receipt confirmed = receiptService.confirmReceipt(100L, sampleUser);

        assertTrue(confirmed.getIsConfirmed());
        assertNotNull(confirmed.getUpdatedAt());
        verify(receiptRepository).updateReceipt(sampleReceipt);
    }

    @Test
    void testConfirmReceipt_NotFound() {
        when(receiptRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            receiptService.confirmReceipt(100L, sampleUser);
        });

        assertEquals("Receipt not found", ex.getMessage());
    }

    @Test
    void testGetReceiptImageUrl_Success() {
        when(receiptRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleReceipt));
        when(s3Service.generatePresignedUrl("dev/1/receipt.jpg")).thenReturn("https://s3.amazonaws.com/presigned-url");

        String url = receiptService.getReceiptImageUrl(100L, sampleUser);

        assertEquals("https://s3.amazonaws.com/presigned-url", url);
    }

    @Test
    void testGetReceiptImageUrl_NotFound() {
        when(receiptRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            receiptService.getReceiptImageUrl(100L, sampleUser);
        });

        assertEquals("Receipt not found", ex.getMessage());
    }
}
