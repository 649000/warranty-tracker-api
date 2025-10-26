package com.nazri.resource;

import com.nazri.model.Claim;
import com.nazri.model.User;
import com.nazri.service.ClaimService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/claim")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClaimResource extends BaseResource {

    @Inject
    ClaimService claimService;

    @GET
    public Response getUserClaims() {
        try {
            User user = validateCurrentUser();
            List<Claim> claims = claimService.findByUserId(user.id);
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
            List<Claim> claims = claimService.findByWarrantyIdAndUserId(warrantyId, user.id);
            return Response.ok(claims).build();
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
            Claim.ClaimStatus claimStatus;
            try {
                claimStatus = Claim.ClaimStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Invalid claim status: " + status, "INVALID_STATUS", Response.Status.BAD_REQUEST.getStatusCode()))
                        .build();
            }
            List<Claim> claims = claimService.findByUserIdAndStatus(user.id, claimStatus);
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
            Claim createdClaim = claimService.createClaim(claim, user);
            return Response.status(Response.Status.CREATED).entity(createdClaim).build();
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
                Claim updatedClaim = claimService.updateClaim(id, claim, user);
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
