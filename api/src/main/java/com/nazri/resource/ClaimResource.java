package com.nazri.resource;

import com.nazri.model.Claim;
import com.nazri.service.ClaimService;
import com.nazri.service.UserService;
import com.nazri.model.User;
import com.nazri.model.Warranty;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/api/claims")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClaimResource {

    @Inject
    ClaimService claimService;
    
    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    // Helper method to get current user
    private Optional<User> getCurrentUser() {
        String firebaseUid = jwt.getSubject();
        return userService.findByFirebaseUid(firebaseUid);
    }

    @GET
    public Response getUserClaims() {
        try {
            Optional<User> user = getCurrentUser();
            
            if (user.isPresent()) {
                // Get all warranties for the user first
                List<Warranty> userWarranties = Warranty.list("user.id", user.get().getId());
                // Extract warranty IDs
                List<Long> warrantyIds = userWarranties.stream()
                        .map(Warranty::getId)
                        .collect(Collectors.toList());
                // Get claims for those warranties
                List<Claim> claims = claimService.findByWarrantyIds(warrantyIds);
                return Response.ok(claims).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving claims: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getClaimById(@PathParam("id") Long id) {
        try {
            Optional<User> user = getCurrentUser();
            
            if (user.isPresent()) {
                Optional<Claim> claim = claimService.findById(id);
                if (claim.isPresent()) {
                    // Check if claim belongs to a warranty owned by the current user
                    Warranty warranty = claim.get().getWarranty();
                    if (warranty.getUser().getId().equals(user.get().getId())) {
                        return Response.ok(claim.get()).build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN)
                                .entity("Access denied: Claim does not belong to user").build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Claim not found with id: " + id).build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving claim: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/warranty/{warrantyId}")
    public Response getClaimsByWarrantyId(@PathParam("warrantyId") Long warrantyId) {
        try {
            Optional<User> user = getCurrentUser();
            
            if (user.isPresent()) {
                // Check if warranty belongs to the current user
                Warranty warranty = Warranty.findById(warrantyId);
                if (warranty != null && warranty.getUser().getId().equals(user.get().getId())) {
                    List<Claim> claims = claimService.findByWarrantyId(warrantyId);
                    return Response.ok(claims).build();
                } else if (warranty == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Warranty not found with id: " + warrantyId).build();
                } else {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("Access denied: Warranty does not belong to user").build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving claims: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/status/{status}")
    public Response getClaimsByStatus(@PathParam("status") String status) {
        try {
            Optional<User> user = getCurrentUser();
            
            if (user.isPresent()) {
                // Get all warranties for the user first
                List<Warranty> userWarranties = Warranty.list("user.id", user.get().getId());
                // Extract warranty IDs
                List<Long> warrantyIds = userWarranties.stream()
                        .map(Warranty::getId)
                        .collect(Collectors.toList());
                // Get claims for those warranties with the specified status
                List<Claim> claims = claimService.findByWarrantyIdsAndStatus(warrantyIds, status);
                return Response.ok(claims).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving claims: " + e.getMessage()).build();
        }
    }

    @POST
    public Response createClaim(Claim claim) {
        try {
            Optional<User> user = getCurrentUser();
            
            if (user.isPresent()) {
                // Check if warranty belongs to the current user
                if (claim.getWarranty() != null) {
                    Warranty warranty = claim.getWarranty();
                    if (warranty.getUser().getId().equals(user.get().getId())) {
                        Claim createdClaim = claimService.createClaim(claim);
                        return Response.status(Response.Status.CREATED).entity(createdClaim).build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN)
                                .entity("Access denied: Warranty does not belong to user").build();
                    }
                } else {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Warranty information is required").build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating claim: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateClaim(@PathParam("id") Long id, Claim claim) {
        try {
            Optional<User> user = getCurrentUser();
            
            if (user.isPresent()) {
                Optional<Claim> existingClaim = claimService.findById(id);
                if (existingClaim.isPresent()) {
                    // Check if claim belongs to a warranty owned by the current user
                    Warranty warranty = existingClaim.get().getWarranty();
                    if (warranty.getUser().getId().equals(user.get().getId())) {
                        // Set the ID to ensure we're updating the correct claim
                        claim.setId(id);
                        Claim updatedClaim = claimService.updateClaim(claim);
                        return Response.ok(updatedClaim).build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN)
                                .entity("Access denied: Claim does not belong to user").build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Claim not found with id: " + id).build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating claim: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteClaim(@PathParam("id") Long id) {
        try {
            Optional<User> user = getCurrentUser();
            
            if (user.isPresent()) {
                Optional<Claim> claim = claimService.findById(id);
                if (claim.isPresent()) {
                    // Check if claim belongs to a warranty owned by the current user
                    Warranty warranty = claim.get().getWarranty();
                    if (warranty.getUser().getId().equals(user.get().getId())) {
                        claimService.deleteClaim(id);
                        return Response.noContent().build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN)
                                .entity("Access denied: Claim does not belong to user").build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Claim not found with id: " + id).build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting claim: " + e.getMessage()).build();
        }
    }
}
