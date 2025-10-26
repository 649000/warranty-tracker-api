package com.nazri.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignGetObjectResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class S3Service {
    
    private static final Logger LOG = Logger.getLogger(S3Service.class);
    
    @Inject
    S3Client s3Client;
    
    @Inject
    S3Presigner s3Presigner;
    
    private final String bucketName;
    private final String environment;
    
    public S3Service() {
        this.bucketName = System.getenv("RECEIPTS_BUCKET_NAME");
        this.environment = System.getenv("ENVIRONMENT") != null ? System.getenv("ENVIRONMENT") : "dev";
    }
    
    public String uploadReceipt(byte[] fileData, Long userId, String fileType) throws Exception {
        try {
            String timestamp = String.valueOf(Instant.now().toEpochMilli());
            String uuid = UUID.randomUUID().toString();
            String fileName = timestamp + "_" + uuid + getFileExtension(fileType);
            String key = environment + "/" + userId + "/" + fileName;
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(fileType)
                    .build();
            
            RequestBody requestBody = RequestBody.fromBytes(fileData);
            s3Client.putObject(putObjectRequest, requestBody);
            
            LOG.infof("Uploaded receipt to S3: %s", key);
            return key;
        } catch (S3Exception e) {
            LOG.error("Error uploading receipt to S3", e);
            throw new Exception("Failed to upload receipt to S3", e);
        }
    }
    
    public String generatePresignedUrl(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();
            
            PresignGetObjectResponse presignedResponse = s3Presigner.presignGetObject(presignRequest);
            LOG.infof("Generated presigned URL for: %s", s3Key);
            return presignedResponse.url().toString();
        } catch (Exception e) {
            LOG.error("Error generating presigned URL", e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
    
    private String getFileExtension(String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/jpg":
                return ".jpg";
            default:
                return ".jpg"; // Default to jpg
        }
    }
}
