package com.nazri.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "warranties", schema = "warranty_tracker")
public class Warranty extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "model_number")
    private String modelNumber;
    
    @Column(name = "serial_number")
    private String serialNumber;
    
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "warranty_period")
    private Integer warrantyPeriod;
    
    @Column(name = "warranty_type")
    private String warrantyType;
    
    @Column(name = "purchase_location")
    private String purchaseLocation;
    
    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;
    
    @Column(name = "receipt_number")
    private String receiptNumber;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @OneToMany(mappedBy = "warranty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Claim> claims;
    
    // Constructors
    public Warranty() {}
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public String getModelNumber() {
        return modelNumber;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public Integer getWarrantyPeriod() {
        return warrantyPeriod;
    }
    
    public String getWarrantyType() {
        return warrantyType;
    }
    
    public String getPurchaseLocation() {
        return purchaseLocation;
    }
    
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public String getReceiptNumber() {
        return receiptNumber;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public String getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public Company getCompany() {
        return company;
    }
    
    public List<Claim> getClaims() {
        return claims;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public void setWarrantyPeriod(Integer warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }
    
    public void setWarrantyType(String warrantyType) {
        this.warrantyType = warrantyType;
    }
    
    public void setPurchaseLocation(String purchaseLocation) {
        this.purchaseLocation = purchaseLocation;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public void setCompany(Company company) {
        this.company = company;
    }
    
    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }
    
    @Override
    public String toString() {
        return "Warranty{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", brand='" + brand + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", warrantyPeriod=" + warrantyPeriod +
                ", warrantyType='" + warrantyType + '\'' +
                ", purchaseLocation='" + purchaseLocation + '\'' +
                ", purchasePrice=" + purchasePrice +
                ", receiptNumber='" + receiptNumber + '\'' +
                ", notes='" + notes + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
