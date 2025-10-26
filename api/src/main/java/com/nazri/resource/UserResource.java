package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.UserService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class UserResource extends BaseResource {

    @Inject
    UserService userService;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    public Response getJwtUser() {
        try {
            User user = validateCurrentUser();
            return Response.ok(user).build();
        } catch (Exception e) {
            if (e instanceof WebApplicationException) {
                throw e;
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving user: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @POST
    public Response createUser() {
        try {
            String firebaseUid = securityIdentity.getPrincipal().getName();

            // Check if user already exists
            if (userService.existsByFirebaseUid(firebaseUid)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(createErrorResponse("User already exists", "USER_EXISTS", Response.Status.CONFLICT.getStatusCode()))
                        .build();
            }

            User createdUser = userService.createUser(
                    firebaseUid,
                    securityIdentity.getAttribute("email"),
                    securityIdentity.getAttribute("preferredUsername")
            );
            return Response.status(Response.Status.CREATED).entity(createdUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error creating user: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @PUT
    public Response updateUser(User userData) {
        try {
            User user = validateCurrentUser();

            // Only update display name from the provided data
            if (userData.getDisplayName() != null) {
                user.setDisplayName(userData.getDisplayName());
            }
            user = userService.updateUser(user);
            return Response.ok(user).build();
        } catch (Exception e) {
            if (e instanceof WebApplicationException) {
                throw e;
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating user: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    // Admin endpoints
    @GET
    @Path("/admin/all")
    @RolesAllowed("admin")
    public Response adminGetAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving users: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/admin/{id}")
    @RolesAllowed("admin")
    public Response adminGetUserById(@PathParam("id") Long id) {
        try {
            Optional<User> userOptional = userService.findUserById(id);
            if (userOptional.isPresent()) {
                return Response.ok(userOptional.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("User not found with id: " + id, "USER_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving user: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @DELETE
    @Path("/admin/{id}")
    @RolesAllowed("admin")
    public Response adminDeleteUser(@PathParam("id") Long id) {
        try {
            boolean deleted = userService.deleteUserById(id);
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("User not found with id: " + id, "USER_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error deleting user: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @PUT
    @Path("/admin/{id}")
    @RolesAllowed("admin")
    public Response adminUpdateUser(@PathParam("id") Long id, User userData) {
        try {
            Optional<User> userOptional = userService.updateUserById(id, userData.getEmail(), userData.getDisplayName());
            if (userOptional.isPresent()) {
                return Response.ok(userOptional.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("User not found with id: " + id, "USER_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating user: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }
}
