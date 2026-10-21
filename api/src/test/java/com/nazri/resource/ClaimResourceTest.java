package com.nazri.resource;

import com.nazri.model.Claim;
import com.nazri.model.User;
import com.nazri.model.Warranty;
import com.nazri.service.ClaimService;
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
public class ClaimResourceTest {

    @Mock
    ClaimService claimService;

    @Mock
    SecurityIdentity securityIdentity;

    @InjectMocks
    ClaimResource claimResource;

    private User sampleUser;
    private Claim sampleClaim;

    @BeforeEach
    void setUp() throws Exception {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        Warranty warranty = new Warranty();
        warranty.id = 10L;
        warranty.setUser(sampleUser);

        sampleClaim = new Claim();
        sampleClaim.id = 100L;
        sampleClaim.setWarranty(warranty);
        sampleClaim.setStatus(Claim.ClaimStatus.SUBMITTED);

        Field baseIdentityField = BaseResource.class.getDeclaredField("securityIdentity");
        baseIdentityField.setAccessible(true);
        baseIdentityField.set(claimResource, securityIdentity);

        lenient().when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);
    }

    @Test
    void testGetUserClaims() {
        when(claimService.findByUserId(1L)).thenReturn(List.of(sampleClaim));

        Response response = claimResource.getUserClaims();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(sampleClaim), response.getEntity());
    }

    @Test
    void testGetClaimById_Found() {
        when(claimService.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleClaim));

        Response response = claimResource.getClaimById(100L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetClaimById_NotFound() {
        when(claimService.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        Response response = claimResource.getClaimById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetClaimsByWarrantyId() {
        when(claimService.findByWarrantyIdAndUserId(10L, 1L)).thenReturn(List.of(sampleClaim));

        Response response = claimResource.getClaimsByWarrantyId(10L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetClaimsByStatus_Valid() {
        when(claimService.findByUserIdAndStatus(1L, Claim.ClaimStatus.SUBMITTED))
                .thenReturn(List.of(sampleClaim));

        Response response = claimResource.getClaimsByStatus("SUBMITTED");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetClaimsByStatus_Invalid() {
        Response response = claimResource.getClaimsByStatus("INVALID_STATUS");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testCreateClaim_Success() {
        when(claimService.createClaim(any(Claim.class), eq(sampleUser))).thenReturn(sampleClaim);

        Response response = claimResource.createClaim(sampleClaim);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    void testCreateClaim_ValidationError() {
        when(claimService.createClaim(any(Claim.class), eq(sampleUser)))
                .thenThrow(new IllegalArgumentException("Warranty is required"));

        Response response = claimResource.createClaim(sampleClaim);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateClaim_Success() {
        when(claimService.updateClaim(eq(100L), any(Claim.class), eq(sampleUser))).thenReturn(sampleClaim);

        Response response = claimResource.updateClaim(100L, sampleClaim);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateClaim_ValidationError() {
        when(claimService.updateClaim(eq(100L), any(Claim.class), eq(sampleUser)))
                .thenThrow(new IllegalArgumentException("Claim not found or does not belong to user"));

        Response response = claimResource.updateClaim(100L, sampleClaim);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteClaim_Success() {
        doNothing().when(claimService).deleteClaim(100L, 1L);

        Response response = claimResource.deleteClaim(100L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteClaim_NotFound() {
        doThrow(new IllegalArgumentException("Claim not found or does not belong to user"))
                .when(claimService).deleteClaim(999L, 1L);

        Response response = claimResource.deleteClaim(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllClaims_Admin() {
        when(claimService.findAllClaims()).thenReturn(List.of(sampleClaim));

        Response response = claimResource.getAllClaims();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testExceptionHandling500() {
        when(claimService.findByUserId(1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), claimResource.getUserClaims().getStatus());

        when(claimService.findByIdAndUserId(100L, 1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), claimResource.getClaimById(100L).getStatus());

        when(claimService.findByWarrantyIdAndUserId(10L, 1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), claimResource.getClaimsByWarrantyId(10L).getStatus());

        when(claimService.findByUserIdAndStatus(eq(1L), any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), claimResource.getClaimsByStatus("SUBMITTED").getStatus());

        when(claimService.createClaim(any(), any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), claimResource.createClaim(sampleClaim).getStatus());

        when(claimService.updateClaim(eq(100L), any(), any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), claimResource.updateClaim(100L, sampleClaim).getStatus());

        doThrow(new RuntimeException("Crash")).when(claimService).deleteClaim(100L, 1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), claimResource.deleteClaim(100L).getStatus());

        when(claimService.findAllClaims()).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), claimResource.getAllClaims().getStatus());
    }
}
