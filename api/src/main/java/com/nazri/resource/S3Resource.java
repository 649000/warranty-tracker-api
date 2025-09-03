package com.nazri.resource;

import com.nazri.model.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import jakarta.inject.Inject;
import java.time.Duration;

@Path("/s3")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class S3Resource extends BaseResource {

    @Inject
    S3Presigner s3Presigner;
    
    @ConfigProperty(name = "app.s3.bucket-name")
    String bucketName;

    @GET
    @Path("/presigned-url")
    public Response getPresignedUrl(@QueryParam("fileName") String fileName) {
        // Validate user is authenticated
        User currentUser = validateCurrentUser();
        
        if (fileName == null || fileName.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"fileName query parameter is required\"}")
                    .build();
        }

        try {
            // Create a PutObjectRequest
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            // Create a PutObjectPresignRequest
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // The URL will expire in 10 minutes
                    .putObjectRequest(objectRequest)
                    .build();

            // Generate the presigned request
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            // Get the URL
            String url = presignedRequest.url().toString();

            return Response.ok("{\"url\": \"" + url + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to generate presigned URL: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
