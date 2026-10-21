package com.nazri.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    S3Client s3Client;

    @Mock
    S3Presigner s3Presigner;

    @InjectMocks
    S3Service s3Service;

    @Test
    void testUploadReceipt_SuccessJpeg() throws Exception {
        byte[] data = new byte[]{1, 2, 3};
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String key = s3Service.uploadReceipt(data, 42L, "image/jpeg");

        assertNotNull(key);
        assertTrue(key.contains("/42/"));
        assertTrue(key.endsWith(".jpg"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadReceipt_SuccessPng() throws Exception {
        byte[] data = new byte[]{4, 5, 6};
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String key = s3Service.uploadReceipt(data, 10L, "image/png");

        assertNotNull(key);
        assertTrue(key.contains("/10/"));
        assertTrue(key.endsWith(".png"));

        String keyJpg = s3Service.uploadReceipt(data, 10L, "image/jpg");
        assertTrue(keyJpg.endsWith(".jpg"));

        String keyDefault = s3Service.uploadReceipt(data, 10L, "application/octet-stream");
        assertTrue(keyDefault.endsWith(".jpg"));
    }

    @Test
    void testUploadReceipt_S3Exception() {
        byte[] data = new byte[]{1};
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Access Denied").build());

        Exception ex = assertThrows(Exception.class, () -> {
            s3Service.uploadReceipt(data, 1L, "image/jpeg");
        });

        assertTrue(ex.getMessage().contains("Failed to upload receipt to S3"));
    }

    @Test
    void testGeneratePresignedUrl_Success() throws Exception {
        PresignedGetObjectRequest mockPresigned = mock(PresignedGetObjectRequest.class);
        when(mockPresigned.url()).thenReturn(new URL("https://s3.amazonaws.com/test-url"));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(mockPresigned);

        String result = s3Service.generatePresignedUrl("dev/1/receipt.jpg");

        assertEquals("https://s3.amazonaws.com/test-url", result);
    }

    @Test
    void testGeneratePresignedUrl_Exception() {
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Signing failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            s3Service.generatePresignedUrl("dev/1/receipt.jpg");
        });

        assertTrue(ex.getMessage().contains("Failed to generate presigned URL"));
    }
}
