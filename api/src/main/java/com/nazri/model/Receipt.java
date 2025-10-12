package com.nazri.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts", schema = "warranty_tracker")
public class Receipt extends PanacheEntity {
    
    @Column(name = "s3_key", nullable = false)
    private String s3Key;
    
    @Column(name = "merchant_name")
    private String merchantName;
    
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    
    @Column(name = "receipt_date")
    private LocalDate receiptDate;
    
    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_product_id", nullable = false)
    @JsonIgnore
    private UserProduct userProduct;
    
    // Constructors
    public Receipt() {}
    
    // Getters
    public String getS3Key() {
        return s3Key;
    }
    
    public String getMerchantName() {
        return merchantName;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public LocalDate getReceiptDate() {
        return receiptDate;
    }
    
    public Boolean getIsConfirmed() {
        return isConfirmed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public UserProduct getUserProduct() {
        return userProduct;
    }
    
    // Setters
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }
    
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public void setReceiptDate(LocalDate receiptDate) {
        this.receiptDate = receiptDate;
    }
    
    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setUserProduct(UserProduct userProduct) {
        this.userProduct = userProduct;
    }
}
