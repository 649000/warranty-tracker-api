package com.nazri.resource;

import com.nazri.model.User;
import com.nazri.service.ReceiptService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/receipts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReceiptResource extends BaseResource {

    @Inject
    ReceiptService receiptService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public RestResponse<?> uploadReceipt(@RestForm("file") FileUpload file,
                                         @RestForm("fileType") String fileType,
                                         @RestForm("userProductId") Long userProductId) {
        try {
            User user = validateCurrentUser();

            // Read file data
            byte[] fileData;
            try {
                fileData = Files.readAllBytes(file.uploadedFile());
            } catch (IOException e) {
                Log.error("Failed to read uploaded file", e);
                return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR,
                        createErrorResponse("Failed to read uploaded file", "FILE_READ_ERROR", 500));
            }

            ReceiptService.ReceiptData receiptData = receiptService.uploadReceipt(fileData, fileType, userProductId, user);
            return RestResponse.ok(receiptData);
        } catch (IllegalArgumentException e) {
            return RestResponse.status(Response.Status.BAD_REQUEST,
                    createErrorResponse(e.getMessage(), "INVALID_INPUT", 400));
        } catch (SecurityException e) {
            return RestResponse.status(Response.Status.FORBIDDEN,
                    createErrorResponse(e.getMessage(), "ACCESS_DENIED", 403));
        } catch (Exception e) {
            Log.error("Failed to process receipt", e);
            return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR,
                    createErrorResponse("Failed to process receipt", "PROCESSING_ERROR", 500));
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
    @Path("/{id}/image-url")
    public Response getReceiptImageUrl(@PathParam("id") Long id) {
        try {
            User user = validateCurrentUser();
            String presignedUrl = receiptService.getReceiptImageUrl(id, user);
            return Response.ok(Map.of("url", presignedUrl)).build();
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
                    .entity(createErrorResponse("Failed to generate receipt image URL", "URL_GENERATION_ERROR", 500))
                    .build();
        }
    }
}
