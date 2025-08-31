package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @GET
    public Response getCurrentUser() {
        try {
            String firebaseUid = jwt.getSubject();
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            if (user.isPresent()) {
                return Response.ok(user.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving user: " + e.getMessage()).build();
        }
    }

    @POST
    public Response createUser() {
        try {
            String firebaseUid = jwt.getSubject();
            
            // Check if user already exists
            if (userService.existsByFirebaseUid(firebaseUid)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("User already exists").build();
            }

            User createdUser = userService.createUser(
                    firebaseUid,
                    jwt.getClaim("email"),
                    jwt.getClaim("preferredUsername")
            );
            return Response.status(Response.Status.CREATED).entity(createdUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating user: " + e.getMessage()).build();
        }
    }

    @PUT
    public Response updateUser(User userData) {
        try {
            String firebaseUid = jwt.getSubject();
            
            // Check if user exists
            Optional<User> existingUser = userService.findByFirebaseUid(firebaseUid);
            if (existingUser.isPresent()) {
                // Only update display name from the provided data
                User updatedUser = existingUser.get();
                if (userData.getDisplayName() != null) {
                    updatedUser.setDisplayName(userData.getDisplayName());
                }
                updatedUser = userService.updateUser(updatedUser);
                return Response.ok(updatedUser).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating user: " + e.getMessage()).build();
        }
    }

    // Admin endpoints
    @GET
    @Path("/admin/all")
    @RolesAllowed("admin")
    public Response getAllUsers() {
        try {
            List<User> users = User.listAll();
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving users: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/admin/{id}")
    @RolesAllowed("admin")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            User user = User.findById(id);
            if (user != null) {
                return Response.ok(user).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving user: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/admin/{id}")
    @RolesAllowed("admin")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            User user = User.findById(id);
            if (user != null) {
                user.delete();
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting user: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/admin/{id}")
    @RolesAllowed("admin")
    public Response adminUpdateUser(@PathParam("id") Long id, User userData) {
        try {
            User existingUser = User.findById(id);
            if (existingUser != null) {
                // Update user fields
                if (userData.getEmail() != null) {
                    existingUser.setEmail(userData.getEmail());
                }
                if (userData.getDisplayName() != null) {
                    existingUser.setDisplayName(userData.getDisplayName());
                }
                existingUser.setUpdatedAt(LocalDateTime.now());
                existingUser.persist();
                return Response.ok(existingUser).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating user: " + e.getMessage()).build();
        }
    }
}
