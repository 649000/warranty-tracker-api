package com.nazri.resource;

import com.nazri.model.Warranty;
import com.nazri.service.WarrantyService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("/api/warranties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WarrantyResource {

    @Inject
    WarrantyService warrantyService;

    @Inject
    JsonWebToken jwt;

    @GET
    public Response getUserWarranties() {
        try {
            // This would require a way to get the user ID from Firebase UID
            // For now, returning not implemented
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented - requires user ID mapping").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getWarrantyById(@PathParam("id") Long id) {
        try {
            Optional<Warranty> warranty = warrantyService.findById(id);
            if (warranty.isPresent()) {
                return Response.ok(warranty.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Warranty not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranty: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/status/{status}")
    public Response getWarrantiesByStatus(@PathParam("status") String status) {
        try {
            // This would require getting the current user's ID
            // For now, returning not implemented
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented - requires user ID mapping").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/expiring")
    public Response getExpiringWarranties(
            @QueryParam("days") @DefaultValue("30") Integer days) {
        try {
            // This would require getting the current user's ID
            // For now, returning not implemented
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented - requires user ID mapping").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @POST
    public Response createWarranty(Warranty warranty) {
        try {
            // This would require setting the current user ID
            // For now, returning not implemented
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented - requires user ID mapping").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating warranty: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateWarranty(@PathParam("id") Long id, Warranty warranty) {
        try {
            // This would require checking if the warranty belongs to the current user
            // For now, returning not implemented
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented - requires user ID mapping").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating warranty: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteWarranty(@PathParam("id") Long id) {
        try {
            // This would require checking if the warranty belongs to the current user
            // For now, returning not implemented
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented - requires user ID mapping").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting warranty: " + e.getMessage()).build();
        }
    }

    // Admin endpoints
    @GET
    @Path("/admin/all")
    @RolesAllowed("admin")
    public Response getAllWarranties() {
        try {
            // This would require a method to get all warranties
            // For now, returning not implemented
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/admin/user/{userId}")
    @RolesAllowed("admin")
    public Response getWarrantiesByUserId(@PathParam("userId") Long userId) {
        try {
            List<Warranty> warranties = warrantyService.findByUserId(userId);
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/admin/company/{companyId}")
    @RolesAllowed("admin")
    public Response getWarrantiesByCompanyId(@PathParam("companyId") Long companyId) {
        try {
            List<Warranty> warranties = warrantyService.findByCompanyId(companyId);
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/admin/expired")
    @RolesAllowed("admin")
    public Response getExpiredWarranties() {
        try {
            List<Warranty> warranties = warrantyService.findExpiredWarranties();
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/admin/expiring")
    @RolesAllowed("admin")
    public Response getExpiringWarrantiesAdmin(
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.now();
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now().plusDays(30);
            
            List<Warranty> warranties = warrantyService.findExpiringBetween(startDate, endDate);
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }
}
