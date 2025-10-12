package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.ReceiptService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Path("/receipts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReceiptResource extends BaseResource {

    @Inject
    ReceiptService receiptService;
    
    public static class ReceiptUploadForm {
        @FormParam("file")
        public byte[] file;
        
        @FormParam("fileType")
        public String fileType;
        
        @FormParam("userProductId")
        public Long userProductId;
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadReceipt(@MultipartForm ReceiptUploadForm form) {
        try {
            User user = validateCurrentUser();
            ReceiptService.ReceiptData receiptData = receiptService.uploadReceipt(form.file, form.fileType, form.userProductId, user);
            return Response.ok(receiptData).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage(), "INVALID_INPUT", 400))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(createErrorResponse(e.getMessage(), "ACCESS_DENIED", 403))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Failed to process receipt", "PROCESSING_ERROR", 500))
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getReceipt(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            Optional<com.nazri.model.Receipt> receipt = receiptService.getReceiptById(id, user);
            if (receipt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Receipt not found", "RECEIPT_NOT_FOUND", 404))
                        .build();
            }
            return Response.ok(receipt.get()).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(createErrorResponse(e.getMessage(), "ACCESS_DENIED", 403))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Failed to retrieve receipt", "RETRIEVAL_ERROR", 500))
                    .build();
        }
    }
    
    @GET
    public Response listReceipts() {
        try {
            User user = validateCurrentUser();
            List<com.nazri.model.Receipt> receipts = receiptService.getReceiptsByUser(user);
            return Response.ok(receipts).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Failed to list receipts", "LIST_ERROR", 500))
                    .build();
        }
    }
    
    @POST
    @Path("/{id}/confirm")
    public Response confirmReceipt(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            com.nazri.model.Receipt receipt = receiptService.confirmReceipt(id, user);
            return Response.ok(receipt).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse(e.getMessage(), "RECEIPT_NOT_FOUND", 404))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(createErrorResponse(e.getMessage(), "ACCESS_DENIED", 403))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Failed to confirm receipt", "CONFIRMATION_ERROR", 500))
                    .build();
        }
    }
    
    @GET
    @Path("/{id}/image")
    @Produces({"image/jpeg", "image/png"})
    public Response getReceiptImage(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            InputStream imageStream = receiptService.getReceiptImage(id, user);
            return Response.ok(imageStream).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse(e.getMessage(), "RECEIPT_NOT_FOUND", 404))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(createErrorResponse(e.getMessage(), "ACCESS_DENIED", 403))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Failed to retrieve receipt image", "IMAGE_RETRIEVAL_ERROR", 500))
                    .build();
        }
    }
}
