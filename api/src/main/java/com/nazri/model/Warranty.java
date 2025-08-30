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
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "warranty_period")
    private Integer warrantyPeriod;
    
    @Column(name = "warranty_type")
    private String warrantyType;
    
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @OneToMany(mappedBy = "warranty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Claim> claims;
    
    // Constructors
    public Warranty() {}
    
    // Getters
    public Long getId() {
        return id;
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
    
    public Product getProduct() {
        return product;
    }
    
    public List<Claim> getClaims() {
        return claims;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
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
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }
    
    @Override
    public String toString() {
        return "Warranty{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", warrantyPeriod=" + warrantyPeriod +
                ", warrantyType='" + warrantyType + '\'' +
                ", notes='" + notes + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
