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
public class ClaimResource extends BaseResource {

    @Inject
    ClaimService claimService;
    
    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @GET
    public Response getUserClaims() {
        try {
            User user = validateCurrentUser();
            // Get all warranties for the user first
            List<Warranty> userWarranties = Warranty.list("user.id", user.getId());
            // Extract warranty IDs
            List<Long> warrantyIds = userWarranties.stream()
                    .map(Warranty::getId)
                    .collect(Collectors.toList());
            // Get claims for those warranties
            List<Claim> claims = claimService.findByWarrantyIds(warrantyIds);
            return Response.ok(claims).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving claims: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getClaimById(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            Optional<Claim> claim = claimService.findById(id);
            if (claim.isPresent()) {
                validateClaimOwnership(claim.get(), user);
                return Response.ok(claim.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Claim not found with id: " + id, "CLAIM_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving claim: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/warranty/{warrantyId}")
    public Response getClaimsByWarrantyId(@PathParam("warrantyId") Long warrantyId) {
        try {
            User user = validateCurrentUser();
            // Check if warranty belongs to the current user
            Warranty warranty = Warranty.findById(warrantyId);
            if (warranty != null && warranty.getUser().getId().equals(user.getId())) {
                List<Claim> claims = claimService.findByWarrantyId(warrantyId);
                return Response.ok(claims).build();
            } else if (warranty == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Warranty not found with id: " + warrantyId, "WARRANTY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            } else {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(createErrorResponse("Access denied: Warranty does not belong to user", "ACCESS_DENIED", Response.Status.FORBIDDEN.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving claims: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/status/{status}")
    public Response getClaimsByStatus(@PathParam("status") String status) {
        try {
            User user = validateCurrentUser();
            // Get all warranties for the user first
            List<Warranty> userWarranties = Warranty.list("user.id", user.getId());
            // Extract warranty IDs
            List<Long> warrantyIds = userWarranties.stream()
                    .map(Warranty::getId)
                    .collect(Collectors.toList());
            // Get claims for those warranties with the specified status
            List<Claim> claims = claimService.findByWarrantyIdsAndStatus(warrantyIds, status);
            return Response.ok(claims).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving claims: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @POST
    public Response createClaim(Claim claim) {
        try {
            User user = validateCurrentUser();
            // Check if warranty belongs to the current user
            if (claim.getWarranty() != null) {
                Warranty warranty = claim.getWarranty();
                if (warranty.getUser().getId().equals(user.getId())) {
                    Claim createdClaim = claimService.createClaim(claim);
                    return Response.status(Response.Status.CREATED).entity(createdClaim).build();
                } else {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity(createErrorResponse("Access denied: Warranty does not belong to user", "ACCESS_DENIED", Response.Status.FORBIDDEN.getStatusCode()))
                            .build();
                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Warranty information is required", "INVALID_INPUT", Response.Status.BAD_REQUEST.getStatusCode()))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage(), "INVALID_INPUT", Response.Status.BAD_REQUEST.getStatusCode()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error creating claim: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateClaim(@PathParam("id") Long id, Claim claim) {
        try {
            User user = validateCurrentUser();
            Optional<Claim> existingClaim = claimService.findById(id);
            if (existingClaim.isPresent()) {
                validateClaimOwnership(existingClaim.get(), user);
                // Set the ID to ensure we're updating the correct claim
                claim.setId(id);
                Claim updatedClaim = claimService.updateClaim(claim);
                return Response.ok(updatedClaim).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Claim not found with id: " + id, "CLAIM_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage(), "INVALID_INPUT", Response.Status.BAD_REQUEST.getStatusCode()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating claim: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteClaim(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            Optional<Claim> claim = claimService.findById(id);
            if (claim.isPresent()) {
                validateClaimOwnership(claim.get(), user);
                claimService.deleteClaim(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Claim not found with id: " + id, "CLAIM_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error deleting claim: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }
}
