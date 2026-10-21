package com.nazri.resource;

import com.nazri.model.Product;
import com.nazri.service.ProductService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductResourceTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductResource productResource;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.id = 1L;
        sampleProduct.setName("Phone");
        sampleProduct.setModelNumber("P-1");
    }

    @Test
    void testGetAllProducts() {
        when(productService.findAllProducts()).thenReturn(List.of(sampleProduct));

        Response response = productResource.getAllProducts();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(sampleProduct), response.getEntity());
    }

    @Test
    void testGetProductById_Found() {
        when(productService.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Response response = productResource.getProductById(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        Response response = productResource.getProductById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testSearchProducts() {
        when(productService.searchProducts("Phone", null, null)).thenReturn(List.of(sampleProduct));

        Response response = productResource.searchProducts("Phone", null, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminCreateProduct_Success() {
        when(productService.createProduct(any(Product.class))).thenReturn(sampleProduct);

        Response response = productResource.adminCreateProduct(sampleProduct);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(sampleProduct, response.getEntity());
    }

    @Test
    void testAdminCreateProduct_MissingNameBadRequest() {
        Product invalid = new Product();
        invalid.setModelNumber("M-1");

        Response response = productResource.adminCreateProduct(invalid);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminCreateProduct_MissingModelNumberBadRequest() {
        Product invalid = new Product();
        invalid.setName("Valid Name");

        Response response = productResource.adminCreateProduct(invalid);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminCreateProduct_ServiceThrowsIllegalArgumentException() {
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Duplicate product"));

        Response response = productResource.adminCreateProduct(sampleProduct);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminUpdateProduct_Success() {
        when(productService.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productService.updateProduct(any(Product.class))).thenReturn(sampleProduct);

        Response response = productResource.adminUpdateProduct(1L, sampleProduct);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminUpdateProduct_NotFound() {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        Response response = productResource.adminUpdateProduct(999L, sampleProduct);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminUpdateProduct_ServiceThrowsIllegalArgumentException() {
        when(productService.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productService.updateProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Invalid product data"));

        Response response = productResource.adminUpdateProduct(1L, sampleProduct);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminDeleteProduct_Success() {
        when(productService.findById(1L)).thenReturn(Optional.of(sampleProduct));
        doNothing().when(productService).deleteProduct(1L);

        Response response = productResource.adminDeleteProduct(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminDeleteProduct_NotFound() {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        Response response = productResource.adminDeleteProduct(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testExceptionHandling500() {
        when(productService.findAllProducts()).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), productResource.getAllProducts().getStatus());

        when(productService.findById(1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), productResource.getProductById(1L).getStatus());

        when(productService.searchProducts(any(), any(), any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), productResource.searchProducts("a", "b", "c").getStatus());

        when(productService.createProduct(any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), productResource.adminCreateProduct(sampleProduct).getStatus());

        doReturn(Optional.of(sampleProduct)).when(productService).findById(1L);
        when(productService.updateProduct(any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), productResource.adminUpdateProduct(1L, sampleProduct).getStatus());

        doThrow(new RuntimeException("Crash")).when(productService).deleteProduct(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), productResource.adminDeleteProduct(1L).getStatus());
    }
}
