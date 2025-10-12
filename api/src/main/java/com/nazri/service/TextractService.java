package com.nazri.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@ApplicationScoped
public class TextractService {
    
    private static final Logger LOG = Logger.getLogger(TextractService.class);
    
    private final TextractClient textractClient;
    
    public TextractService() {
        this.textractClient = TextractClient.builder().build();
    }
    
    public Map<String, Object> extractReceiptData(String bucketName, String key) throws Exception {
        try {
            DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                    .document(Document.builder()
                            .s3Object(S3Object.builder()
                                    .bucket(bucketName)
                                    .name(key)
                                    .build())
                            .build())
                    .build();
            
            DetectDocumentTextResponse response = textractClient.detectDocumentText(request);
            List<Block> blocks = response.blocks();
            
            return parseReceiptData(blocks);
        } catch (TextractException e) {
            LOG.error("Error extracting text from receipt", e);
            throw new Exception("Failed to extract text from receipt", e);
        }
    }
    
    private Map<String, Object> parseReceiptData(List<Block> blocks) {
        Map<String, Object> result = new HashMap<>();
        StringBuilder textBuilder = new StringBuilder();
        
        // Combine all text blocks
        for (Block block : blocks) {
            if (block.blockType() == BlockType.LINE) {
                textBuilder.append(block.text()).append(" ");
            }
        }
        
        String fullText = textBuilder.toString().toLowerCase();
        LOG.debugf("Extracted text: %s", fullText);
        
        // Try to extract merchant name (look for common patterns)
        String merchant = extractMerchantName(fullText);
        result.put("merchantName", merchant);
        
        // Try to extract total amount (look for total patterns)
        BigDecimal total = extractTotalAmount(fullText);
        result.put("totalAmount", total);
        
        // Try to extract date
        LocalDate date = extractDate(fullText);
        result.put("receiptDate", date);
        
        return result;
    }
    
    private String extractMerchantName(String text) {
        // Look for common merchant indicators
        String[] lines = text.split("\\s+");
        if (lines.length > 0) {
            // Return first few words as merchant name
            StringBuilder merchant = new StringBuilder();
            int wordCount = Math.min(3, lines.length);
            for (int i = 0; i < wordCount; i++) {
                merchant.append(lines[i]).append(" ");
            }
            return merchant.toString().trim();
        }
        return "Unknown Merchant";
    }
    
    private BigDecimal extractTotalAmount(String text) {
        // Simple pattern matching for total amounts
        String[] patterns = {"total\\s*[$£€]\\s*(\\d+\\.\\d{2})", "total\\s*(\\d+\\.\\d{2})", "\\$(\\d+\\.\\d{2})"};
        
        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(text);
            if (m.find()) {
                try {
                    return new BigDecimal(m.group(1));
                } catch (NumberFormatException e) {
                    // Continue to next pattern
                }
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    private LocalDate extractDate(String text) {
        // Common date formats
        String[] dateFormats = {"MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "MM-dd-yyyy"};
        
        for (String format : dateFormats) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            String[] words = text.split("\\s+");
            for (String word : words) {
                try {
                    return LocalDate.parse(word, formatter);
                } catch (DateTimeParseException e) {
                    // Try next format
                }
            }
        }
        
        return LocalDate.now(); // Default to today if no date found
    }
}
