package com.nazri.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "claims", schema = "warranty_tracker")
public class Claim extends PanacheEntity {

    public enum ClaimStatus {
        SUBMITTED, PROCESSING, APPROVED, DENIED, COMPLETED
    }

    @Column(name = "claim_date", nullable = false)
    private LocalDateTime claimDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "issue_description")
    private String issueDescription;

    @Column(name = "resolution_details")
    private String resolutionDetails;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warranty_id", nullable = false)
    private Warranty warranty;

    // Constructors
    public Claim() {
    }

    // Getters
    public LocalDateTime getClaimDate() {
        return claimDate;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public String getResolutionDetails() {
        return resolutionDetails;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Warranty getWarranty() {
        return warranty;
    }

    // Setters
    public void setClaimDate(LocalDateTime claimDate) {
        this.claimDate = claimDate;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public void setResolutionDetails(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
    }

    @Override
    public String toString() {
        return "Claim{" +
                "id=" + id +
                ", claimDate=" + claimDate +
                ", status='" + status + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", issueDescription='" + issueDescription + '\'' +
                ", resolutionDetails='" + resolutionDetails + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
