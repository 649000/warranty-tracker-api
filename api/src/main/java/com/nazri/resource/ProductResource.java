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

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    public Response getAllProducts() {
        try {
            List<Product> products = Product.listAll();
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving products: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long id) {
        try {
            Product product = Product.findById(id);
            if (product != null) {
                return Response.ok(product).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving product: " + e.getMessage()).build();
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
                products = Product.listAll();
            }
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error searching products: " + e.getMessage()).build();
        }
    }

    @POST
    @RolesAllowed("admin")
    public Response createProduct(Product product) {
        try {
            // Check if product with same name and model number already exists
            List<Product> existingProducts = Product.find(
                    "name = ?1 and modelNumber = ?2", 
                    product.getName(), 
                    product.getModelNumber()).list();
            
            if (!existingProducts.isEmpty()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Product with this name and model number already exists").build();
            }

            product = productService.updateProduct(product);
            return Response.status(Response.Status.CREATED).entity(product).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating product: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response updateProduct(@PathParam("id") Long id, Product product) {
        try {
            Product existingProduct = Product.findById(id);
            if (existingProduct != null) {
                // Set the ID to ensure we're updating the correct product
                product.setId(id);
                product = productService.updateProduct(product);
                return Response.ok(product).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating product: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteProduct(@PathParam("id") Long id) {
        try {
            Product product = Product.findById(id);
            if (product != null) {
                productService.deleteProduct(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product not found with id: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting product: " + e.getMessage()).build();
        }
    }
}
