package com.nazri.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "companies", schema = "warranty_tracker")
public class Company extends PanacheEntity {

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
    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Warranty> warranties;

    // Constructors
    public Company() {
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClaimProcess() {
        return claimProcess;
    }

    public void setClaimProcess(String claimProcess) {
        this.claimProcess = claimProcess;
    }

    public String getClaimUrl() {
        return claimUrl;
    }

    public void setClaimUrl(String claimUrl) {
        this.claimUrl = claimUrl;
    }

    public String getSupportHours() {
        return supportHours;
    }

    public void setSupportHours(String supportHours) {
        this.supportHours = supportHours;
    }

    public String getReturnInstructions() {
        return returnInstructions;
    }

    public void setReturnInstructions(String returnInstructions) {
        this.returnInstructions = returnInstructions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Warranty> getWarranties() {
        return warranties;
    }

    public void setWarranties(List<Warranty> warranties) {
        this.warranties = warranties;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
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
                ", id=" + id +
                '}';
    }
}
