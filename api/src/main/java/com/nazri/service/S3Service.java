package com.nazri.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class S3Service {
    
    private static final Logger LOG = Logger.getLogger(S3Service.class);
    
    private final S3Client s3Client;
    private final String bucketName;
    private final String environment;
    
    public S3Service() {
        this.s3Client = S3Client.builder().build();
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
    
    public InputStream downloadReceipt(String s3Key) throws Exception {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            
            ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest);
            LOG.infof("Downloaded receipt from S3: %s", s3Key);
            return inputStream;
        } catch (S3Exception e) {
            LOG.error("Error downloading receipt from S3", e);
            throw new Exception("Failed to download receipt from S3", e);
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
