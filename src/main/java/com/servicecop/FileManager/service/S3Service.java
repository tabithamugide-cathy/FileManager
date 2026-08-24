package com.servicecop.FileManager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {
    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    // List of allowed image MIME types
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    // Max file size in bytes
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;


    public void uploadFile(MultipartFile file) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getOriginalFilename())
                        .build(),
                RequestBody.fromBytes(file.getBytes()));
    }

public byte[] downloadFile(String key){
    ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build());
    return objectAsBytes.asByteArray();
}

}
