package com.nazri.resource;

import com.nazri.model.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.lang.reflect.Field;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ResourceTest {

    @Mock
    S3Presigner s3Presigner;

    @Mock
    SecurityIdentity securityIdentity;

    @InjectMocks
    S3Resource s3Resource;

    private User sampleUser;

    @BeforeEach
    void setUp() throws Exception {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        Field baseIdentityField = BaseResource.class.getDeclaredField("securityIdentity");
        baseIdentityField.setAccessible(true);
        baseIdentityField.set(s3Resource, securityIdentity);

        Field bucketNameField = S3Resource.class.getDeclaredField("bucketName");
        bucketNameField.setAccessible(true);
        bucketNameField.set(s3Resource, "test-bucket");

        lenient().when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);
    }

    @Test
    void testGetPresignedUrl_Success() throws Exception {
        PresignedPutObjectRequest presignedResponse = mock(PresignedPutObjectRequest.class);
        when(presignedResponse.url()).thenReturn(new URL("https://s3.amazonaws.com/test-bucket/file.jpg"));
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedResponse);

        Response response = s3Resource.getPresignedUrl("file.jpg");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("https://s3.amazonaws.com/test-bucket/file.jpg"));
    }

    @Test
    void testGetPresignedUrl_MissingFileName() {
        Response response1 = s3Resource.getPresignedUrl(null);
        Response response2 = s3Resource.getPresignedUrl("   ");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response1.getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response2.getStatus());
    }

    @Test
    void testGetPresignedUrl_PresignerThrowsException() {
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Presigning failed"));

        Response response = s3Resource.getPresignedUrl("file.jpg");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
