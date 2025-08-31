package com.nazri.dto;

import java.time.LocalDateTime;

public class CompanyDTO {
    public Long id;
    public String name;
    public String contactPhone;
    public String contactEmail;
    public String website;
    public String address;
    public String claimProcess;
    public String claimUrl;
    public String supportHours;
    public String returnInstructions;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public CompanyDTO() {
    }

    public CompanyDTO(Long id, String name, String contactPhone, String contactEmail, 
                     String website, String address, String claimProcess, String claimUrl,
                     String supportHours, String returnInstructions, 
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.website = website;
        this.address = address;
        this.claimProcess = claimProcess;
        this.claimUrl = claimUrl;
        this.supportHours = supportHours;
        this.returnInstructions = returnInstructions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "CompanyDTO{" +
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
