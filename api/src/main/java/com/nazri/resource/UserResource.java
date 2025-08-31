package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

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
}
