package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusPrincipal;
import jakarta.ws.rs.WebApplicationException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {

    @Mock
    UserService userService;

    @Mock
    SecurityIdentity securityIdentity;

    @InjectMocks
    UserResource userResource;

    private User sampleUser;

    @BeforeEach
    void setUp() throws Exception {
        sampleUser = new User("uid-123", "user@example.com", "John Doe");
        sampleUser.id = 1L;

        // BaseResource fields
        Field baseIdentityField = BaseResource.class.getDeclaredField("securityIdentity");
        baseIdentityField.setAccessible(true);
        baseIdentityField.set(userResource, securityIdentity);
    }

    @Test
    void testGetJwtUser_Success() {
        when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);

        Response response = userResource.getJwtUser();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(sampleUser, response.getEntity());
    }

    @Test
    void testGetJwtUser_UserNotFoundThrows404() {
        when(securityIdentity.getAttribute("user")).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            userResource.getJwtUser();
        });

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void testCreateUser_Success() {
        when(securityIdentity.getPrincipal()).thenReturn(new QuarkusPrincipal("uid-new"));
        when(userService.existsByFirebaseUid("uid-new")).thenReturn(false);
        when(securityIdentity.getAttribute("email")).thenReturn("new@example.com");
        when(securityIdentity.getAttribute("preferredUsername")).thenReturn("NewUser");

        User created = new User("uid-new", "new@example.com", "NewUser");
        created.id = 2L;
        when(userService.createUser("uid-new", "new@example.com", "NewUser")).thenReturn(created);

        Response response = userResource.createUser();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(created, response.getEntity());
    }

    @Test
    void testCreateUser_AlreadyExists_Conflict() {
        when(securityIdentity.getPrincipal()).thenReturn(new QuarkusPrincipal("uid-existing"));
        when(userService.existsByFirebaseUid("uid-existing")).thenReturn(true);

        Response response = userResource.createUser();

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateUser_Success() {
        when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);
        when(userService.updateUser(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updateData = new User();
        updateData.setDisplayName("Jane Doe");

        Response response = userResource.updateUser(updateData);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        User updated = (User) response.getEntity();
        assertEquals("Jane Doe", updated.getDisplayName());
    }

    @Test
    void testUpdateUser_UserNotFoundThrowsWebApplicationException() {
        when(securityIdentity.getAttribute("user")).thenReturn(null);

        jakarta.ws.rs.WebApplicationException ex = assertThrows(jakarta.ws.rs.WebApplicationException.class, () -> {
            userResource.updateUser(sampleUser);
        });

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void testAdminGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser));

        Response response = userResource.adminGetAllUsers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(sampleUser), response.getEntity());
    }

    @Test
    void testAdminGetUserById_Found() {
        when(userService.findUserById(1L)).thenReturn(Optional.of(sampleUser));

        Response response = userResource.adminGetUserById(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(sampleUser, response.getEntity());
    }

    @Test
    void testAdminGetUserById_NotFound() {
        when(userService.findUserById(999L)).thenReturn(Optional.empty());

        Response response = userResource.adminGetUserById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminDeleteUser_Success() {
        when(userService.deleteUserById(1L)).thenReturn(true);

        Response response = userResource.adminDeleteUser(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminDeleteUser_NotFound() {
        when(userService.deleteUserById(999L)).thenReturn(false);

        Response response = userResource.adminDeleteUser(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminUpdateUser_Found() {
        User updateData = new User();
        updateData.setEmail("updated@example.com");
        updateData.setDisplayName("Updated");

        when(userService.updateUserById(1L, "updated@example.com", "Updated"))
                .thenReturn(Optional.of(sampleUser));

        Response response = userResource.adminUpdateUser(1L, updateData);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminUpdateUser_NotFound() {
        User updateData = new User();
        when(userService.updateUserById(999L, null, null)).thenReturn(Optional.empty());

        Response response = userResource.adminUpdateUser(999L, updateData);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testInternalErrorHandling() {
        when(securityIdentity.getPrincipal()).thenThrow(new RuntimeException("Crash"));

        Response response = userResource.createUser();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        when(userService.getAllUsers()).thenThrow(new RuntimeException("DB crash"));
        Response responseAdmin = userResource.adminGetAllUsers();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseAdmin.getStatus());

        when(securityIdentity.getAttribute("user")).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userResource.getJwtUser().getStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userResource.updateUser(sampleUser).getStatus());

        when(userService.findUserById(1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userResource.adminGetUserById(1L).getStatus());

        when(userService.deleteUserById(1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userResource.adminDeleteUser(1L).getStatus());

        when(userService.updateUserById(eq(1L), any(), any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), userResource.adminUpdateUser(1L, sampleUser).getStatus());
    }
}
