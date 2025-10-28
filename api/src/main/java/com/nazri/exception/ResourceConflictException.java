package com.nazri.exception;

public class ResourceConflictException extends RuntimeException {

    private final String resourceType;
    private final String reason;

    public ResourceConflictException(String resourceType, String reason) {
        super("Conflict for " + resourceType + ": " + reason);
        this.resourceType = resourceType;
        this.reason = reason;
    }

    public ResourceConflictException(String message) {
        super(message);
        this.resourceType = "Resource";
        this.reason = "General conflict";
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getReason() {
        return reason;
    }
}
