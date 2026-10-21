package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.model.Warranty;
import com.nazri.service.WarrantyService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarrantyResourceTest {

    @Mock
    WarrantyService warrantyService;

    @Mock
    SecurityIdentity securityIdentity;

    @InjectMocks
    WarrantyResource warrantyResource;

    private User sampleUser;
    private Warranty sampleWarranty;

    @BeforeEach
    void setUp() throws Exception {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        sampleWarranty = new Warranty();
        sampleWarranty.id = 100L;
        sampleWarranty.setUser(sampleUser);
        sampleWarranty.setStatus(Warranty.WarrantyStatus.ACTIVE);

        Field baseIdentityField = BaseResource.class.getDeclaredField("securityIdentity");
        baseIdentityField.setAccessible(true);
        baseIdentityField.set(warrantyResource, securityIdentity);

        lenient().when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);
    }

    @Test
    void testGetUserWarranties() {
        when(warrantyService.findByUserId(1L)).thenReturn(List.of(sampleWarranty));

        Response response = warrantyResource.getUserWarranties();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(sampleWarranty), response.getEntity());
    }

    @Test
    void testGetWarrantyById_Found() {
        when(warrantyService.findById(1L, 100L)).thenReturn(Optional.of(sampleWarranty));

        Response response = warrantyResource.getWarrantyById(100L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetWarrantyById_NotFound() {
        when(warrantyService.findById(1L, 999L)).thenReturn(Optional.empty());

        Response response = warrantyResource.getWarrantyById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetWarrantiesByStatus_Valid() {
        when(warrantyService.findByUserIdAndStatus(1L, Warranty.WarrantyStatus.ACTIVE))
                .thenReturn(List.of(sampleWarranty));

        Response response = warrantyResource.getWarrantiesByStatus("ACTIVE");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetWarrantiesByStatus_Invalid() {
        Response response = warrantyResource.getWarrantiesByStatus("INVALID_STATUS");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetExpiringWarranties() {
        when(warrantyService.findExpiringWarranties(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(sampleWarranty));

        Response response = warrantyResource.getExpiringWarranties(30);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testCreateWarranty_Success() {
        when(warrantyService.createWarranty(any(Warranty.class))).thenReturn(sampleWarranty);

        Response response = warrantyResource.createWarranty(sampleWarranty);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    void testCreateWarranty_ValidationError() {
        when(warrantyService.createWarranty(any(Warranty.class)))
                .thenThrow(new IllegalArgumentException("Invalid date range"));

        Response response = warrantyResource.createWarranty(sampleWarranty);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateWarranty_Success() {
        when(warrantyService.updateWarranty(eq(1L), any(Warranty.class))).thenReturn(sampleWarranty);

        Response response = warrantyResource.updateWarranty(100L, sampleWarranty);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateWarranty_ValidationError() {
        when(warrantyService.updateWarranty(eq(1L), any(Warranty.class)))
                .thenThrow(new IllegalArgumentException("Warranty not found or does not belong to user"));

        Response response = warrantyResource.updateWarranty(100L, sampleWarranty);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteWarranty_Success() {
        when(warrantyService.findById(1L, 100L)).thenReturn(Optional.of(sampleWarranty));
        doNothing().when(warrantyService).deleteWarranty(1L, 100L);

        Response response = warrantyResource.deleteWarranty(100L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteWarranty_NotFound() {
        when(warrantyService.findById(1L, 999L)).thenReturn(Optional.empty());

        Response response = warrantyResource.deleteWarranty(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminGetAllWarranties() {
        when(warrantyService.findAllWarranties()).thenReturn(List.of(sampleWarranty));

        Response response = warrantyResource.adminGetAllWarranties();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminGetWarrantiesByUserId() {
        when(warrantyService.findByUserId(1L)).thenReturn(List.of(sampleWarranty));

        Response response = warrantyResource.adminGetWarrantiesByUserId(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminGetWarrantiesByCompanyId() {
        when(warrantyService.findByCompanyId(10L)).thenReturn(List.of(sampleWarranty));

        Response response = warrantyResource.adminGetWarrantiesByCompanyId(10L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminGetExpiredWarranties() {
        when(warrantyService.findExpiredWarranties()).thenReturn(List.of(sampleWarranty));

        Response response = warrantyResource.adminGetExpiredWarranties();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(sampleWarranty), response.getEntity());
    }

    @Test
    void testAdminGetExpiringWarranties_WithAndWithoutDates() {
        when(warrantyService.findExpiringWarranties(isNull(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(sampleWarranty));

        Response response1 = warrantyResource.adminGetExpiringWarranties(null, null);
        assertEquals(Response.Status.OK.getStatusCode(), response1.getStatus());

        Response response2 = warrantyResource.adminGetExpiringWarranties("2025-01-01", "2025-06-01");
        assertEquals(Response.Status.OK.getStatusCode(), response2.getStatus());
    }

    @Test
    void testExceptionHandlingReturns500() {
        when(warrantyService.findByUserId(1L)).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.getUserWarranties().getStatus());

        when(warrantyService.findById(1L, 100L)).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.getWarrantyById(100L).getStatus());

        when(warrantyService.findByUserIdAndStatus(eq(1L), any())).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.getWarrantiesByStatus("ACTIVE").getStatus());

        when(warrantyService.findExpiringWarranties(eq(1L), any(), any())).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.getExpiringWarranties(30).getStatus());

        when(warrantyService.createWarranty(any())).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.createWarranty(sampleWarranty).getStatus());

        when(warrantyService.updateWarranty(eq(1L), any())).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.updateWarranty(100L, sampleWarranty).getStatus());

        doReturn(Optional.of(sampleWarranty)).when(warrantyService).findById(1L, 100L);
        doThrow(new RuntimeException("DB down")).when(warrantyService).deleteWarranty(1L, 100L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.deleteWarranty(100L).getStatus());

        when(warrantyService.findAllWarranties()).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.adminGetAllWarranties().getStatus());

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.adminGetWarrantiesByUserId(1L).getStatus());

        when(warrantyService.findByCompanyId(10L)).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.adminGetWarrantiesByCompanyId(10L).getStatus());

        when(warrantyService.findExpiredWarranties()).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.adminGetExpiredWarranties().getStatus());

        when(warrantyService.findExpiringWarranties(isNull(), any(), any())).thenThrow(new RuntimeException("DB down"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), warrantyResource.adminGetExpiringWarranties(null, null).getStatus());
    }
}
