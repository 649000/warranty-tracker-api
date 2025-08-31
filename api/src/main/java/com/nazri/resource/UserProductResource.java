package com.nazri.resource;

import com.nazri.model.UserProduct;
import com.nazri.service.UserProductService;
import com.nazri.service.UserService;
import com.nazri.model.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.List;
import java.util.Optional;

@Path("/api/user-products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserProductResource extends BaseResource {

    @Inject
    UserProductService userProductService;
    
    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @GET
    public Response getUserProducts() {
        try {
            User user = validateCurrentUser();
            List<UserProduct> userProducts = userProductService.findByUserId(user.getId());
            return Response.ok(userProducts).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving user products: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUserProductById(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            UserProduct userProduct = UserProduct.findById(id);
            if (userProduct != null) {
                validateUserProductOwnership(userProduct, user);
                return Response.ok(userProduct).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User product not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving user product: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/product/{productId}")
    public Response getUserProductsByProductId(@PathParam("productId") Long productId) {
        try {
            User user = validateCurrentUser();
            List<UserProduct> userProducts = userProductService.findByUserIdAndProductId(
                user.getId(), productId);
            return Response.ok(userProducts).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving user products: " + e.getMessage()).build();
        }
    }

    @POST
    public Response createUserProduct(UserProduct userProduct) {
        try {
            User user = validateCurrentUser();
            
            // Check if a user product with the same serial number already exists for this user
            if (userProduct.getSerialNumber() != null && 
                userProductService.existsByUserIdAndSerialNumber(user.getId(), userProduct.getSerialNumber())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("User product with this serial number already exists for this user").build();
            }
            
            // Set the current user as the owner
            userProduct.setUser(user);
            UserProduct createdUserProduct = userProductService.createUserProduct(userProduct);
            return Response.status(Response.Status.CREATED).entity(createdUserProduct).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating user product: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUserProduct(@PathParam("id") Long id, UserProduct userProduct) {
        try {
            User user = validateCurrentUser();
            UserProduct existingUserProduct = UserProduct.findById(id);
            if (existingUserProduct != null) {
                validateUserProductOwnership(existingUserProduct, user);
                
                // Check if changing to a serial number that already exists for this user
                if (userProduct.getSerialNumber() != null && 
                    !userProduct.getSerialNumber().equals(existingUserProduct.getSerialNumber()) &&
                    userProductService.existsByUserIdAndSerialNumber(user.getId(), userProduct.getSerialNumber())) {
                    return Response.status(Response.Status.CONFLICT)
                            .entity("User product with this serial number already exists for this user").build();
                }
                
                // Set the ID and user to ensure we're updating the correct user product
                userProduct.setId(id);
                userProduct.setUser(user);
                UserProduct updatedUserProduct = userProductService.updateUserProduct(userProduct);
                return Response.ok(updatedUserProduct).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User product not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating user product: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUserProduct(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            UserProduct userProduct = UserProduct.findById(id);
            if (userProduct != null) {
                validateUserProductOwnership(userProduct, user);
                userProductService.deleteUserProduct(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User product not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting user product: " + e.getMessage()).build();
        }
    }
}
