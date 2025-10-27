package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.model.Warranty;
import com.nazri.service.UserService;
import com.nazri.service.WarrantyService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("/warranty")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class WarrantyResource extends BaseResource {

    @Inject
    WarrantyService warrantyService;

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @GET
    public Response getUserWarranties() {
        try {
            User user = validateCurrentUser();
            List<Warranty> warranties = warrantyService.findByUserId(user.id);
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranties: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getWarrantyById(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            Optional<Warranty> warranty = warrantyService.findById(id);
            if (warranty.isPresent()) {
                validateWarrantyOwnership(warranty.get(), user);
                return Response.ok(warranty.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Warranty not found with id: " + id, "WARRANTY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranty: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/status/{status}")
    public Response getWarrantiesByStatus(@PathParam("status") String status) {
        try {
            User user = validateCurrentUser();
            Warranty.WarrantyStatus warrantyStatus = Warranty.WarrantyStatus.valueOf(status.toUpperCase());
            List<Warranty> warranties = warrantyService.findByUserIdAndStatus(user.id, warrantyStatus);
            return Response.ok(warranties).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("Invalid status value: " + status, "INVALID_STATUS", Response.Status.BAD_REQUEST.getStatusCode()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranties: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/expiring")
    public Response getExpiringWarranties(
            @QueryParam("days") @DefaultValue("30") Integer days) {
        try {
            User user = validateCurrentUser();
            LocalDate endDate = LocalDate.now().plusDays(days);
            List<Warranty> warranties = warrantyService.findExpiringWarranties(user.id, LocalDate.now(), endDate);
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranties: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @POST
    public Response createWarranty(Warranty warranty) {
        try {
            User user = validateCurrentUser();
            // Set the current user as the warranty owner
            warranty.setUser(user);
            Warranty createdWarranty = warrantyService.createWarranty(warranty);
            return Response.status(Response.Status.CREATED).entity(createdWarranty).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage(), "INVALID_INPUT", Response.Status.BAD_REQUEST.getStatusCode()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error creating warranty: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateWarranty(@PathParam("id") Long id, Warranty warranty) {
        try {
            User user = validateCurrentUser();
            Optional<Warranty> existingWarranty = warrantyService.findById(id);
            if (existingWarranty.isPresent()) {
                validateWarrantyOwnership(existingWarranty.get(), user);
                // Set the ID and user to ensure we're updating the correct warranty
                warranty.id = id;
                warranty.setUser(user);
                Warranty updatedWarranty = warrantyService.updateWarranty(warranty);
                return Response.ok(updatedWarranty).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Warranty not found with id: " + id, "WARRANTY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage(), "INVALID_INPUT", Response.Status.BAD_REQUEST.getStatusCode()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating warranty: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteWarranty(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            Optional<Warranty> warranty = warrantyService.findById(id);
            if (warranty.isPresent()) {
                validateWarrantyOwnership(warranty.get(), user);
                warrantyService.deleteWarranty(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Warranty not found with id: " + id, "WARRANTY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error deleting warranty: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    // Admin endpoints with /admin prefix
    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    public Response adminGetAllWarranties() {
        try {
            // Admin can see all warranties
            List<Warranty> warranties = warrantyService.findAllWarranties();
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranties: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/admin/user/{userId}")
    @RolesAllowed("admin")
    public Response adminGetWarrantiesByUserId(@PathParam("userId") Long userId) {
        try {
            List<Warranty> warranties = warrantyService.findByUserId(userId);
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranties: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/admin/company/{companyId}")
    @RolesAllowed("admin")
    public Response adminGetWarrantiesByCompanyId(@PathParam("companyId") Long companyId) {
        try {
            List<Warranty> warranties = warrantyService.findByCompanyId(companyId);
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranties: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/admin/expired")
    @RolesAllowed("admin")
    public Response adminGetExpiredWarranties() {
        try {
            List<Warranty> warranties = warrantyService.findExpiredWarranties();
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranties: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/admin/expiring")
    @RolesAllowed("admin")
    public Response adminGetExpiringWarranties(
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.now();
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now().plusDays(30);

            List<Warranty> warranties = warrantyService.findExpiringBetween(startDate, endDate);
            return Response.ok(warranties).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving warranties: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }
}
