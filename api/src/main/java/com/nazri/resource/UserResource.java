package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @GET
    public Response getAllUsers() {
        try {
            List<User> users = userService.findAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving users: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                return Response.ok(user.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with id: " + id).build();
            }
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
    public Response createUser(User user) {
        try {
            User createdUser = userService.createUser(
                    user.getFirebaseUid(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getPhotoUrl()
            );
            return Response.status(Response.Status.CREATED).entity(createdUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating user: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (existingUser.isPresent()) {
                user.setId(id);
                User updatedUser = userService.updateUser(user);
                return Response.ok(updatedUser).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating user: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                userService.deleteUser(id);
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
}
