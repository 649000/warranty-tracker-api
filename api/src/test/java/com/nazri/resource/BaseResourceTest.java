package com.nazri.resource;

import com.nazri.model.*;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BaseResourceTest {

    @Mock
    SecurityIdentity securityIdentity;

    private TestableBaseResource resource;
    private User sampleUser;

    static class TestableBaseResource extends BaseResource {
        // Concrete subclass to test protected methods
    }

    @BeforeEach
    void setUp() throws Exception {
        resource = new TestableBaseResource();
        Field field = BaseResource.class.getDeclaredField("securityIdentity");
        field.setAccessible(true);
        field.set(resource, securityIdentity);

        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;
    }

    @Test
    void testGetCurrentUser_Present() {
        when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);

        Optional<User> result = resource.getCurrentUser();

        assertTrue(result.isPresent());
        assertEquals(sampleUser, result.get());
    }

    @Test
    void testGetCurrentUser_NotPresent() {
        when(securityIdentity.getAttribute("user")).thenReturn(null);

        Optional<User> result = resource.getCurrentUser();

        assertFalse(result.isPresent());
    }

    @Test
    void testGetCurrentUser_AttributeNotUserInstance() {
        when(securityIdentity.getAttribute("user")).thenReturn("not a user object");

        Optional<User> result = resource.getCurrentUser();

        assertFalse(result.isPresent());
    }

    @Test
    void testValidateCurrentUser_Found() {
        when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);

        User user = resource.validateCurrentUser();

        assertEquals(sampleUser, user);
    }

    @Test
    void testValidateCurrentUser_NotFoundThrows404() {
        when(securityIdentity.getAttribute("user")).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.validateCurrentUser();
        });

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void testValidateWarrantyOwnership_Success() {
        Warranty warranty = new Warranty();
        warranty.setUser(sampleUser);

        assertDoesNotThrow(() -> resource.validateWarrantyOwnership(warranty, sampleUser));
    }

    @Test
    void testValidateWarrantyOwnership_Forbidden() {
        User otherUser = new User();
        otherUser.id = 2L;

        Warranty warranty = new Warranty();
        warranty.setUser(otherUser);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.validateWarrantyOwnership(warranty, sampleUser);
        });

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void testValidateUserProductOwnership_Success() {
        UserProduct up = new UserProduct();
        up.setUser(sampleUser);

        assertDoesNotThrow(() -> resource.validateUserProductOwnership(up, sampleUser));
    }

    @Test
    void testValidateUserProductOwnership_Forbidden() {
        User otherUser = new User();
        otherUser.id = 2L;

        UserProduct up = new UserProduct();
        up.setUser(otherUser);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.validateUserProductOwnership(up, sampleUser);
        });

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void testValidateClaimOwnership_Success() {
        Warranty warranty = new Warranty();
        warranty.setUser(sampleUser);

        Claim claim = new Claim();
        claim.setWarranty(warranty);

        assertDoesNotThrow(() -> resource.validateClaimOwnership(claim, sampleUser));
    }

    @Test
    void testValidateClaimOwnership_Forbidden() {
        User otherUser = new User();
        otherUser.id = 2L;

        Warranty warranty = new Warranty();
        warranty.setUser(otherUser);

        Claim claim = new Claim();
        claim.setWarranty(warranty);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.validateClaimOwnership(claim, sampleUser);
        });

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void testCreateErrorResponse() {
        BaseResource.JsonApiErrorResponse err = resource.createErrorResponse("Details", "ERROR_CODE", 400);

        assertNotNull(err.errors);
        assertEquals(1, err.errors.size());
        assertEquals("Details", err.errors.get(0).detail);
        assertEquals("ERROR_CODE", err.errors.get(0).code);
        assertEquals("400", err.errors.get(0).status);
        assertNotNull(err.errors.get(0).id);
    }
}
