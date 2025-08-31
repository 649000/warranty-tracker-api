package com.nazri.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_products", schema = "warranty_tracker")
public class UserProduct extends PanacheEntity {
    
    @Column(name = "serial_number", unique = true)
    private String serialNumber;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_price")
    private java.math.BigDecimal purchasePrice;
    
    @Column(name = "purchase_location")
    private String purchaseLocation;
    
    @Column(name = "receipt_number")
    private String receiptNumber;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "userProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Warranty> warranties;
    
    // Constructors
    public UserProduct() {}
    
    // Getters
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    
    public java.math.BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public String getPurchaseLocation() {
        return purchaseLocation;
    }
    
    public String getReceiptNumber() {
        return receiptNumber;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public User getUser() {
        return user;
    }
    
    public List<Warranty> getWarranties() {
        return warranties;
    }
    
    // Setters
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public void setPurchasePrice(java.math.BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public void setPurchaseLocation(String purchaseLocation) {
        this.purchaseLocation = purchaseLocation;
    }
    
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public void setWarranties(List<Warranty> warranties) {
        this.warranties = warranties;
    }
    
    @Override
    public String toString() {
        return "UserProduct{" +
                "id=" + id +
                ", serialNumber='" + serialNumber + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", purchasePrice=" + purchasePrice +
                ", purchaseLocation='" + purchaseLocation + '\'' +
                ", receiptNumber='" + receiptNumber + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
