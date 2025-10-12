package com.nazri.service;

import com.nazri.model.Receipt;
import com.nazri.model.User;
import com.nazri.model.UserProduct;
import com.nazri.repository.ReceiptRepository;
import com.nazri.repository.UserProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ReceiptService {
    
    private static final Logger LOG = Logger.getLogger(ReceiptService.class);
    
    @Inject
    ReceiptRepository receiptRepository;
    
    @Inject
    UserProductRepository userProductRepository;
    
    @Inject
    S3Service s3Service;
    
    @Inject
    TextractService textractService;
    
    private final String bucketName;
    
    public ReceiptService() {
        this.bucketName = System.getenv("RECEIPTS_BUCKET_NAME");
    }
    
    public ReceiptData uploadReceipt(byte[] fileData, String fileType, Long userProductId, User user) throws Exception {
        // Validate file type
        if (!isValidFileType(fileType)) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG and PNG files are allowed.");
        }
        
        // Validate file size (max 10MB)
        if (fileData.length > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB.");
        }
        
        // Validate user product exists and belongs to user
        Optional<UserProduct> userProductOpt = userProductRepository.findByIdOptional(userProductId);
        if (userProductOpt.isEmpty()) {
            throw new IllegalArgumentException("User product not found");
        }
        
        UserProduct userProduct = userProductOpt.get();
        if (!userProduct.getUser().id.equals(user.id)) {
            throw new SecurityException("Access denied: User product does not belong to user");
        }
        
        // Upload to S3
        String s3Key = s3Service.uploadReceipt(fileData, user.id, fileType);
        
        // Create initial receipt record
        Receipt receipt = new Receipt();
        receipt.setS3Key(s3Key);
        receipt.setUserProduct(userProduct);
        receipt.setIsConfirmed(false);
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setUpdatedAt(LocalDateTime.now());
        
        receipt = receiptRepository.createReceipt(receipt);
        
        // Process with Textract
        Map<String, Object> extractedData = textractService.extractReceiptData(bucketName, s3Key);
        
        // Update receipt with extracted data
        receipt.setMerchantName((String) extractedData.get("merchantName"));
        receipt.setTotalAmount((BigDecimal) extractedData.get("totalAmount"));
        receipt.setReceiptDate((LocalDate) extractedData.get("receiptDate"));
        receipt.setUpdatedAt(LocalDateTime.now());
        
        receipt = receiptRepository.updateReceipt(receipt);
        
        return new ReceiptData(
                receipt.id,
                receipt.getMerchantName(),
                receipt.getTotalAmount(),
                receipt.getReceiptDate(),
                receipt.getS3Key(),
                receipt.getIsConfirmed()
        );
    }
    
    public Optional<Receipt> getReceiptById(Long id, User user) {
        Optional<Receipt> receipt = receiptRepository.findByIdOptional(id);
        if (receipt.isPresent() && !receipt.get().getUserProduct().getUser().id.equals(user.id)) {
            throw new SecurityException("Access denied: Receipt does not belong to user");
        }
        return receipt;
    }
    
    public List<Receipt> getReceiptsByUser(User user) {
        return receiptRepository.findByUserId(user.id);
    }
    
    @Transactional
    public Receipt confirmReceipt(Long receiptId, User user) {
        Optional<Receipt> optionalReceipt = receiptRepository.findByIdOptional(receiptId);
        if (optionalReceipt.isEmpty()) {
            throw new IllegalArgumentException("Receipt not found");
        }
        
        Receipt receipt = optionalReceipt.get();
        if (!receipt.getUserProduct().getUser().id.equals(user.id)) {
            throw new SecurityException("Access denied: Receipt does not belong to user");
        }
        
        receipt.setIsConfirmed(true);
        receipt.setUpdatedAt(LocalDateTime.now());
        
        return receiptRepository.updateReceipt(receipt);
    }
    
    public String getReceiptImageUrl(Long receiptId, User user) {
        Optional<Receipt> optionalReceipt = receiptRepository.findByIdOptional(receiptId);
        if (optionalReceipt.isEmpty()) {
            throw new IllegalArgumentException("Receipt not found");
        }
        
        Receipt receipt = optionalReceipt.get();
        if (!receipt.getUserProduct().getUser().id.equals(user.id)) {
            throw new SecurityException("Access denied: Receipt does not belong to user");
        }
        
        return s3Service.generatePresignedUrl(receipt.getS3Key());
    }
    
    private boolean isValidFileType(String fileType) {
        return "image/jpeg".equals(fileType) || 
               "image/jpg".equals(fileType) || 
               "image/png".equals(fileType);
    }
    
    public static class ReceiptData {
        public final Long id;
        public final String merchantName;
        public final BigDecimal totalAmount;
        public final LocalDate receiptDate;
        public final String s3Key;
        public final Boolean isConfirmed;
        
        public ReceiptData(Long id, String merchantName, BigDecimal totalAmount, 
                          LocalDate receiptDate, String s3Key, Boolean isConfirmed) {
            this.id = id;
            this.merchantName = merchantName;
            this.totalAmount = totalAmount;
            this.receiptDate = receiptDate;
            this.s3Key = s3Key;
            this.isConfirmed = isConfirmed;
        }
    }
}
