package com.nazri.resource;

import com.nazri.model.Product;
import com.nazri.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource extends BaseResource {

    @Inject
    ProductService productService;

    @GET
    public Response getAllProducts() {
        try {
            List<Product> products = productService.findAllProducts();
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving products: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long id) {
        try {
            Optional<Product> product = productService.findById(id);
            if (product.isPresent()) {
                return Response.ok(product.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Product not found with id: " + id, "PRODUCT_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving product: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/search")
    public Response searchProducts(
            @QueryParam("name") String name,
            @QueryParam("brand") String brand,
            @QueryParam("modelNumber") String modelNumber) {
        try {
            List<Product> products;
            if (name != null && !name.isEmpty()) {
                products = productService.findByNameContaining(name);
            } else if (brand != null && !brand.isEmpty()) {
                products = productService.findByBrandContaining(brand);
            } else if (modelNumber != null && !modelNumber.isEmpty()) {
                products = productService.findByModelNumberContaining(modelNumber);
            } else {
                products = productService.findAllProducts();
            }
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error searching products: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @POST
    @RolesAllowed("admin")
    public Response createProduct(Product product) {
        try {
            // Check if product with same name and model number already exists
            List<Product> existingProducts = productService.findByNameAndModelNumber(product.getName(), product.getModelNumber());

            if (!existingProducts.isEmpty()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(createErrorResponse("Product with this name and model number already exists", "PRODUCT_EXISTS", Response.Status.CONFLICT.getStatusCode()))
                        .build();
            }

            Product createdProduct = productService.createProduct(product);
            return Response.status(Response.Status.CREATED).entity(createdProduct).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error creating product: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response updateProduct(@PathParam("id") Long id, Product product) {
        try {
            Optional<Product> existingProduct = productService.findById(id);
            if (existingProduct.isPresent()) {
                // Set the ID to ensure we're updating the correct product
                product.id = id;
                Product updatedProduct = productService.updateProduct(product);
                return Response.ok(updatedProduct).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Product not found with id: " + id, "PRODUCT_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating product: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteProduct(@PathParam("id") Long id) {
        try {
            Optional<Product> product = productService.findById(id);
            if (product.isPresent()) {
                productService.deleteProduct(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Product not found with id: " + id, "PRODUCT_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error deleting product: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }
}
