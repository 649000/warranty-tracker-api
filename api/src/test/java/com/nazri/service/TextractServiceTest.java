package com.nazri.service;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TextractServiceTest {

    @Test
    void testExtractReceiptData() throws Exception {
        TextractClient mockClient = mock(TextractClient.class);
        TextractService service = new TextractService(mockClient);

        Block line1 = Block.builder()
                .blockType(BlockType.LINE)
                .text("Walmart Supercenter")
                .build();
        Block line2 = Block.builder()
                .blockType(BlockType.LINE)
                .text("Date: 2025-05-15")
                .build();
        Block line3 = Block.builder()
                .blockType(BlockType.LINE)
                .text("total $45.67")
                .build();

        DetectDocumentTextResponse response = DetectDocumentTextResponse.builder()
                .blocks(List.of(line1, line2, line3))
                .build();

        when(mockClient.detectDocumentText(any(DetectDocumentTextRequest.class))).thenReturn(response);

        Map<String, Object> result = service.extractReceiptData("my-bucket", "receipt.jpg");

        assertNotNull(result);
        assertEquals("walmart supercenter date:", result.get("merchantName"));
        assertEquals(new BigDecimal("45.67"), result.get("totalAmount"));
        assertEquals(LocalDate.of(2025, 5, 15), result.get("receiptDate"));
    }

    @Test
    void testExtractReceiptData_TextractException() throws Exception {
        TextractClient mockClient = mock(TextractClient.class);
        TextractService service = new TextractService(mockClient);

        when(mockClient.detectDocumentText(any(DetectDocumentTextRequest.class)))
                .thenThrow(TextractException.builder().message("Invalid image").build());

        Exception ex = assertThrows(Exception.class, () -> {
            service.extractReceiptData("bucket", "key");
        });

        assertTrue(ex.getMessage().contains("Failed to extract text from receipt"));
    }

    @Test
    void testExtractReceiptData_NoAmountNoDate_BlankText() throws Exception {
        TextractClient mockClient = mock(TextractClient.class);
        TextractService service = new TextractService(mockClient);

        Block blankLine = Block.builder()
                .blockType(BlockType.LINE)
                .text("   ")
                .build();

        DetectDocumentTextResponse response = DetectDocumentTextResponse.builder()
                .blocks(List.of(blankLine))
                .build();

        when(mockClient.detectDocumentText(any(DetectDocumentTextRequest.class))).thenReturn(response);

        Map<String, Object> result = service.extractReceiptData("bucket", "key");

        assertNotNull(result);
        assertEquals("Unknown Merchant", result.get("merchantName"));
        assertEquals(BigDecimal.ZERO, result.get("totalAmount"));
        assertEquals(LocalDate.now(), result.get("receiptDate"));
    }

    @Test
    void testExtractReceiptData_AlternativeFormats() throws Exception {
        TextractClient mockClient = mock(TextractClient.class);
        TextractService service = new TextractService(mockClient);

        Block line1 = Block.builder()
                .blockType(BlockType.LINE)
                .text("Shop 12/31/2024 total 99.50")
                .build();

        DetectDocumentTextResponse response = DetectDocumentTextResponse.builder()
                .blocks(List.of(line1))
                .build();

        when(mockClient.detectDocumentText(any(DetectDocumentTextRequest.class))).thenReturn(response);

        Map<String, Object> result = service.extractReceiptData("bucket", "key");

        assertNotNull(result);
        assertEquals(new BigDecimal("99.50"), result.get("totalAmount"));
        assertEquals(LocalDate.of(2024, 12, 31), result.get("receiptDate"));
    }
}
