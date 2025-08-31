package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.UserService;
import jakarta.annotation.security.RolesAllowed;
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
    @RolesAllowed("admin")
    public Response getAllUsers() {
        try {
            // Since there's no findAllUsers in UserService, we'll need to implement this
            // or use a different approach. For now, let's return not implemented.
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving users: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            // Since there's no findById in UserService, we'll need to implement this
            // or use a different approach. For now, let's return not implemented.
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving user: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/firebase/{firebaseUid}")
    public Response getUserByFirebaseUid(@PathParam("firebaseUid") String firebaseUid) {
        try {
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            if (user.isPresent()) {
                return Response.ok(user.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with firebase UID: " + firebaseUid).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving user: " + e.getMessage()).build();
        }
    }

    @POST
    public Response createUser() {

        try {
            // Check if user already exists
            if (userService.existsByFirebaseUid(jwt.getSubject())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("User already exists with firebase UID: " + jwt.getSubject()).build();
            }

            User createdUser = userService.createUser(
                    jwt.getSubject(),
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
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User userData) {
        try {
            // Since there's no findById in UserService, we can't check if user exists
            // We'll need to implement this properly or use a different approach.
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating user: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            // Since there's no deleteUser in UserService, we'll need to implement this
            // or use a different approach. For now, let's return not implemented.
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("Not implemented").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting user: " + e.getMessage()).build();
        }
    }
}
