package com.nazri.resource;

import com.nazri.model.Receipt;
import com.nazri.model.User;
import com.nazri.service.ReceiptService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceiptResourceTest {

    @Mock
    ReceiptService receiptService;

    @Mock
    SecurityIdentity securityIdentity;

    @InjectMocks
    ReceiptResource receiptResource;

    private User sampleUser;
    private Receipt sampleReceipt;

    @BeforeEach
    void setUp() throws Exception {
        sampleUser = new User("uid-1", "user@example.com");
        sampleUser.id = 1L;

        sampleReceipt = new Receipt();
        sampleReceipt.id = 100L;
        sampleReceipt.setS3Key("dev/1/receipt.jpg");

        Field baseIdentityField = BaseResource.class.getDeclaredField("securityIdentity");
        baseIdentityField.setAccessible(true);
        baseIdentityField.set(receiptResource, securityIdentity);

        lenient().when(securityIdentity.getAttribute("user")).thenReturn(sampleUser);
    }

    @Test
    void testUploadReceipt_Success() throws Exception {
        File tempFile = File.createTempFile("receipt", ".jpg");
        tempFile.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(new byte[]{1, 2, 3});
        }

        FileUpload mockFileUpload = mock(FileUpload.class);
        when(mockFileUpload.uploadedFile()).thenReturn(tempFile.toPath());

        ReceiptService.ReceiptData data = new ReceiptService.ReceiptData(
                100L, "Walmart", new BigDecimal("50.00"), LocalDate.now(), "key", false);

        when(receiptService.uploadReceipt(any(), eq("image/jpeg"), eq(10L), eq(sampleUser)))
                .thenReturn(data);

        RestResponse<?> response = receiptResource.uploadReceipt(mockFileUpload, "image/jpeg", 10L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(data, response.getEntity());
    }

    @Test
    void testUploadReceipt_IllegalArgumentException() throws Exception {
        File tempFile = File.createTempFile("receipt", ".pdf");
        tempFile.deleteOnExit();

        FileUpload mockFileUpload = mock(FileUpload.class);
        when(mockFileUpload.uploadedFile()).thenReturn(tempFile.toPath());

        when(receiptService.uploadReceipt(any(), eq("application/pdf"), eq(10L), eq(sampleUser)))
                .thenThrow(new IllegalArgumentException("Invalid file type"));

        RestResponse<?> response = receiptResource.uploadReceipt(mockFileUpload, "application/pdf", 10L);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testUploadReceipt_SecurityException() throws Exception {
        File tempFile = File.createTempFile("receipt", ".jpg");
        tempFile.deleteOnExit();

        FileUpload mockFileUpload = mock(FileUpload.class);
        when(mockFileUpload.uploadedFile()).thenReturn(tempFile.toPath());

        when(receiptService.uploadReceipt(any(), eq("image/jpeg"), eq(10L), eq(sampleUser)))
                .thenThrow(new SecurityException("Access denied"));

        RestResponse<?> response = receiptResource.uploadReceipt(mockFileUpload, "image/jpeg", 10L);

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetReceipt_Found() {
        when(receiptService.getReceiptById(100L, sampleUser)).thenReturn(Optional.of(sampleReceipt));

        Response response = receiptResource.getReceipt(100L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(sampleReceipt, response.getEntity());
    }

    @Test
    void testGetReceipt_NotFound() {
        when(receiptService.getReceiptById(999L, sampleUser)).thenReturn(Optional.empty());

        Response response = receiptResource.getReceipt(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testListReceipts() {
        when(receiptService.getReceiptsByUser(sampleUser)).thenReturn(List.of(sampleReceipt));

        Response response = receiptResource.listReceipts();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(sampleReceipt), response.getEntity());
    }

    @Test
    void testConfirmReceipt_Success() {
        when(receiptService.confirmReceipt(100L, sampleUser)).thenReturn(sampleReceipt);

        Response response = receiptResource.confirmReceipt(100L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(sampleReceipt, response.getEntity());
    }

    @Test
    void testConfirmReceipt_NotFound() {
        when(receiptService.confirmReceipt(999L, sampleUser))
                .thenThrow(new IllegalArgumentException("Receipt not found"));

        Response response = receiptResource.confirmReceipt(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetReceiptImageUrl_Success() {
        when(receiptService.getReceiptImageUrl(100L, sampleUser)).thenReturn("https://s3.amazonaws.com/image.jpg");

        Response response = receiptResource.getReceiptImageUrl(100L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<?, ?> body = (Map<?, ?>) response.getEntity();
        assertEquals("https://s3.amazonaws.com/image.jpg", body.get("url"));
    }

    @Test
    void testGetReceiptImageUrl_NotFound() {
        when(receiptService.getReceiptImageUrl(999L, sampleUser))
                .thenThrow(new IllegalArgumentException("Receipt not found"));

        Response response = receiptResource.getReceiptImageUrl(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testExceptionHandlingRemaining() throws Exception {
        // uploadReceipt unexpected exception
        File tempFile = File.createTempFile("receipt", ".jpg");
        tempFile.deleteOnExit();
        FileUpload mockFileUpload = mock(FileUpload.class);
        when(mockFileUpload.uploadedFile()).thenReturn(tempFile.toPath());
        when(receiptService.uploadReceipt(any(), any(), any(), any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                receiptResource.uploadReceipt(mockFileUpload, "image/jpeg", 10L).getStatus());

        // uploadReceipt file read error
        when(mockFileUpload.uploadedFile()).thenReturn(Path.of("/non/existent/path/for/sure"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                receiptResource.uploadReceipt(mockFileUpload, "image/jpeg", 10L).getStatus());

        // getReceipt SecurityException & Exception
        when(receiptService.getReceiptById(100L, sampleUser)).thenThrow(new SecurityException("Denied"));
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), receiptResource.getReceipt(100L).getStatus());

        doThrow(new RuntimeException("Crash")).when(receiptService).getReceiptById(100L, sampleUser);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), receiptResource.getReceipt(100L).getStatus());

        // listReceipts Exception
        when(receiptService.getReceiptsByUser(sampleUser)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), receiptResource.listReceipts().getStatus());

        // confirmReceipt SecurityException & Exception
        when(receiptService.confirmReceipt(100L, sampleUser)).thenThrow(new SecurityException("Denied"));
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), receiptResource.confirmReceipt(100L).getStatus());

        doThrow(new RuntimeException("Crash")).when(receiptService).confirmReceipt(100L, sampleUser);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), receiptResource.confirmReceipt(100L).getStatus());

        // getReceiptImageUrl SecurityException & Exception
        when(receiptService.getReceiptImageUrl(100L, sampleUser)).thenThrow(new SecurityException("Denied"));
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), receiptResource.getReceiptImageUrl(100L).getStatus());

        doThrow(new RuntimeException("Crash")).when(receiptService).getReceiptImageUrl(100L, sampleUser);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), receiptResource.getReceiptImageUrl(100L).getStatus());
    }
}
