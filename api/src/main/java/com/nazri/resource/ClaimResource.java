package com.nazri.resource;

import com.nazri.model.Claim;
import com.nazri.service.ClaimService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/api/claims")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClaimResource {

    @Inject
    ClaimService claimService;

    @GET
    public Response getAllClaims() {
        try {
            // This would require a findAll method in ClaimService
            // For now, we'll return not implemented
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving claims: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getClaimById(@PathParam("id") Long id) {
        try {
            return claimService.findById(id)
                    .map(claim -> Response.ok(claim).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity("Claim not found with id: " + id).build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving claim: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/warranty/{warrantyId}")
    public Response getClaimsByWarrantyId(@PathParam("warrantyId") Long warrantyId) {
        try {
            List<Claim> claims = claimService.findByWarrantyId(warrantyId);
            return Response.ok(claims).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving claims: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/status/{status}")
    public Response getClaimsByStatus(@PathParam("status") String status) {
        try {
            List<Claim> claims = claimService.findByStatus(status);
            return Response.ok(claims).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving claims: " + e.getMessage()).build();
        }
    }

    @POST
    public Response createClaim(Claim claim) {
        try {
            Claim createdClaim = claimService.createClaim(claim);
            return Response.status(Response.Status.CREATED).entity(createdClaim).build();
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
            // Check if claim exists
            return claimService.findById(id)
                    .map(existingClaim -> {
                        // Set the ID to ensure we're updating the correct claim
                        claim.setId(id);
                        Claim updatedClaim = claimService.updateClaim(claim);
                        return Response.ok(updatedClaim).build();
                    })
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity("Claim not found with id: " + id).build());
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
            // Check if claim exists
            return claimService.findById(id)
                    .map(claim -> {
                        claimService.deleteClaim(id);
                        return Response.noContent().build();
                    })
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity("Claim not found with id: " + id).build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting claim: " + e.getMessage()).build();
        }
    }
}
