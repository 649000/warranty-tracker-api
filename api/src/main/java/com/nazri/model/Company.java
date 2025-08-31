package com.nazri.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "companies", schema = "warranty_tracker")
public class Company extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "claim_process")
    private String claimProcess; // Description of how to make claims
    
    @Column(name = "claim_url")
    private String claimUrl; // URL for online claims
    
    @Column(name = "support_hours")
    private String supportHours;
    
    @Column(name = "return_instructions")
    private String returnInstructions;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Warranty> warranties;
    
    // Constructors
    public Company() {}
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getClaimProcess() {
        return claimProcess;
    }
    
    public String getClaimUrl() {
        return claimUrl;
    }
    
    public String getSupportHours() {
        return supportHours;
    }
    
    public String getReturnInstructions() {
        return returnInstructions;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public List<Warranty> getWarranties() {
        return warranties;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setClaimProcess(String claimProcess) {
        this.claimProcess = claimProcess;
    }
    
    public void setClaimUrl(String claimUrl) {
        this.claimUrl = claimUrl;
    }
    
    public void setSupportHours(String supportHours) {
        this.supportHours = supportHours;
    }
    
    public void setReturnInstructions(String returnInstructions) {
        this.returnInstructions = returnInstructions;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setWarranties(List<Warranty> warranties) {
        this.warranties = warranties;
    }
    
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", website='" + website + '\'' +
                ", address='" + address + '\'' +
                ", claimProcess='" + claimProcess + '\'' +
                ", claimUrl='" + claimUrl + '\'' +
                ", supportHours='" + supportHours + '\'' +
                ", returnInstructions='" + returnInstructions + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
