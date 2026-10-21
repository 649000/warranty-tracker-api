package com.nazri.resource;

import com.nazri.model.Product;
import com.nazri.model.User;
import com.nazri.model.UserProduct;
import com.nazri.service.UserProductService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProductResourceTest {

    @Mock
    UserProductService userProductService;

    @Mock
    SecurityIdentity securityIdentity;

    @InjectMocks
    UserProductResource userProductResource;

    private User sampleUser;
    private UserProduct sampleUserProduct;

    @BeforeEach
    void setUp() throws Exception {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        Product product = new Product();
        product.id = 10L;

        sampleUserProduct = new UserProduct();
        sampleUserProduct.id = 50L;
        sampleUserProduct.setUser(sampleUser);
        sampleUserProduct.setProduct(product);
        sampleUserProduct.setSerialNumber("SN-001");

        Field baseIdentityField = BaseResource.class.getDeclaredField("securityIdentity");
        baseIdentityField.setAccessible(true);
        baseIdentityField.set(userProductResource, securityIdentity);

        lenient().when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);
    }

    @Test
    void testGetUserProducts() {
        when(userProductService.findByUserId(1L)).thenReturn(List.of(sampleUserProduct));

        Response response = userProductResource.getUserProducts();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(sampleUserProduct), response.getEntity());
    }

    @Test
    void testGetUserProductById_Found() {
        when(userProductService.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleUserProduct));

        Response response = userProductResource.getUserProductById(50L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetUserProductById_NotFound() {
        when(userProductService.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        Response response = userProductResource.getUserProductById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetUserProductsByProductId() {
        when(userProductService.findByUserIdAndProductId(1L, 10L)).thenReturn(List.of(sampleUserProduct));

        Response response = userProductResource.getUserProductsByProductId(10L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testCreateUserProduct_Success() {
        when(userProductService.createUserProduct(any(UserProduct.class))).thenReturn(sampleUserProduct);

        Response response = userProductResource.createUserProduct(sampleUserProduct);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    void testCreateUserProduct_ValidationError() {
        when(userProductService.createUserProduct(any(UserProduct.class)))
                .thenThrow(new IllegalArgumentException("Duplicate serial number"));

        Response response = userProductResource.createUserProduct(sampleUserProduct);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateUserProduct_Success() {
        when(userProductService.updateUserProduct(any(UserProduct.class), eq(1L))).thenReturn(sampleUserProduct);

        Response response = userProductResource.updateUserProduct(50L, sampleUserProduct);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateUserProduct_ValidationError() {
        when(userProductService.updateUserProduct(any(UserProduct.class), eq(1L)))
                .thenThrow(new IllegalArgumentException("User product not found"));

        Response response = userProductResource.updateUserProduct(50L, sampleUserProduct);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteUserProduct_Found() {
        when(userProductService.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(sampleUserProduct));
        doNothing().when(userProductService).deleteUserProduct(50L, 1L);

        Response response = userProductResource.deleteUserProduct(50L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteUserProduct_NotFound() {
        when(userProductService.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        Response response = userProductResource.deleteUserProduct(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testExceptionHandling500() {
        when(userProductService.findByUserId(1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userProductResource.getUserProducts().getStatus());

        when(userProductService.findByIdAndUserId(50L, 1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userProductResource.getUserProductById(50L).getStatus());

        when(userProductService.findByUserIdAndProductId(1L, 10L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userProductResource.getUserProductsByProductId(10L).getStatus());

        when(userProductService.createUserProduct(any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userProductResource.createUserProduct(sampleUserProduct).getStatus());

        when(userProductService.updateUserProduct(any(), eq(1L))).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userProductResource.updateUserProduct(50L, sampleUserProduct).getStatus());

        doReturn(Optional.of(sampleUserProduct)).when(userProductService).findByIdAndUserId(50L, 1L);
        doThrow(new RuntimeException("Crash")).when(userProductService).deleteUserProduct(50L, 1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userProductResource.deleteUserProduct(50L).getStatus());
    }
}
