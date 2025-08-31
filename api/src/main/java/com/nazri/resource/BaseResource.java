package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.UserService;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.inject.Inject;
import java.util.Optional;

/**
 * Base resource class providing common functionality for all resource classes
 */
public abstract class BaseResource {

    @Inject
    protected UserService userService;

    @Inject
    protected JsonWebToken jwt;

    /**
     * Helper method to get current user from JWT token
     * @return Optional containing the user if found
     */
    protected Optional<User> getCurrentUser() {
        String firebaseUid = jwt.getSubject();
        return userService.findByFirebaseUid(firebaseUid);
    }
    
    /**
     * Helper method to validate current user exists
     * @return User object if found
     * @throws WebApplicationException if user not found
     */
    protected User validateCurrentUser() {
        return getCurrentUser()
            .orElseThrow(() -> new WebApplicationException(
                Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found").build()));
    }
    
    /**
     * Helper method to validate warranty ownership
     * @param warranty the warranty to check
     * @param user the user to validate against
     * @throws WebApplicationException if user doesn't own the warranty
     */
    protected void validateWarrantyOwnership(com.nazri.model.Warranty warranty, User user) {
        if (!warranty.getUser().getId().equals(user.getId())) {
            throw new WebApplicationException(
                Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: Warranty does not belong to user").build());
        }
    }
    
    /**
     * Helper method to validate user product ownership
     * @param userProduct the user product to check
     * @param user the user to validate against
     * @throws WebApplicationException if user doesn't own the user product
     */
    protected void validateUserProductOwnership(com.nazri.model.UserProduct userProduct, User user) {
        if (!userProduct.getUser().getId().equals(user.getId())) {
            throw new WebApplicationException(
                Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: User product does not belong to user").build());
        }
    }
    
    /**
     * Helper method to validate claim ownership
     * @param claim the claim to check
     * @param user the user to validate against
     * @throws WebApplicationException if user doesn't own the claim
     */
    protected void validateClaimOwnership(com.nazri.model.Claim claim, User user) {
        if (!claim.getWarranty().getUser().getId().equals(user.getId())) {
            throw new WebApplicationException(
                Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: Claim does not belong to user").build());
        }
    }
}
