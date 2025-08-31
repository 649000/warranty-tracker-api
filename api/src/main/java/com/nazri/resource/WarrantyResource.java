package com.nazri.resource;

import com.nazri.model.Warranty;
import com.nazri.service.WarrantyService;
import com.nazri.service.UserService;
import com.nazri.model.User;
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
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @GET
    public Response getUserWarranties() {
        try {
            String firebaseUid = jwt.getSubject();
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            
            if (user.isPresent()) {
                List<Warranty> warranties = warrantyService.findByUserId(user.get().getId());
                return Response.ok(warranties).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getWarrantyById(@PathParam("id") Long id) {
        try {
            String firebaseUid = jwt.getSubject();
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            
            if (user.isPresent()) {
                Optional<Warranty> warranty = warrantyService.findById(id);
                if (warranty.isPresent()) {
                    // Check if warranty belongs to the current user
                    if (warranty.get().getUser().getId().equals(user.get().getId())) {
                        return Response.ok(warranty.get()).build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN)
                                .entity("Access denied: Warranty does not belong to user").build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Warranty not found with id: " + id).build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
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
            String firebaseUid = jwt.getSubject();
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            
            if (user.isPresent()) {
                List<Warranty> warranties = warrantyService.findByUserId(user.get().getId());
                // Filter by status
                List<Warranty> filteredWarranties = warranties.stream()
                        .filter(w -> w.getStatus().equals(status))
                        .toList();
                return Response.ok(filteredWarranties).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
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
            String firebaseUid = jwt.getSubject();
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            
            if (user.isPresent()) {
                LocalDate endDate = LocalDate.now().plusDays(days);
                List<Warranty> warranties = warrantyService.findByUserId(user.get().getId());
                // Filter expiring warranties
                List<Warranty> expiringWarranties = warranties.stream()
                        .filter(w -> w.getEndDate().isAfter(LocalDate.now()) && 
                                w.getEndDate().isBefore(endDate))
                        .toList();
                return Response.ok(expiringWarranties).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving warranties: " + e.getMessage()).build();
        }
    }

    @POST
    public Response createWarranty(Warranty warranty) {
        try {
            String firebaseUid = jwt.getSubject();
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            
            if (user.isPresent()) {
                // Set the current user as the warranty owner
                warranty.setUser(user.get());
                Warranty createdWarranty = warrantyService.createWarranty(warranty);
                return Response.status(Response.Status.CREATED).entity(createdWarranty).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating warranty: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateWarranty(@PathParam("id") Long id, Warranty warranty) {
        try {
            String firebaseUid = jwt.getSubject();
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            
            if (user.isPresent()) {
                Optional<Warranty> existingWarranty = warrantyService.findById(id);
                if (existingWarranty.isPresent()) {
                    // Check if warranty belongs to the current user
                    if (existingWarranty.get().getUser().getId().equals(user.get().getId())) {
                        // Set the ID and user to ensure we're updating the correct warranty
                        warranty.setId(id);
                        warranty.setUser(user.get());
                        Warranty updatedWarranty = warrantyService.updateWarranty(warranty);
                        return Response.ok(updatedWarranty).build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN)
                                .entity("Access denied: Warranty does not belong to user").build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Warranty not found with id: " + id).build();
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
                    .entity("Error updating warranty: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteWarranty(@PathParam("id") Long id) {
        try {
            String firebaseUid = jwt.getSubject();
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            
            if (user.isPresent()) {
                Optional<Warranty> warranty = warrantyService.findById(id);
                if (warranty.isPresent()) {
                    // Check if warranty belongs to the current user
                    if (warranty.get().getUser().getId().equals(user.get().getId())) {
                        warrantyService.deleteWarranty(id);
                        return Response.noContent().build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN)
                                .entity("Access denied: Warranty does not belong to user").build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Warranty not found with id: " + id).build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
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
            // Admin can see all warranties
            List<Warranty> warranties = Warranty.listAll();
            return Response.ok(warranties).build();
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
