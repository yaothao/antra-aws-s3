package com.example.awss3.service.impl;

import com.example.awss3.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final S3Client s3Client;
    private final String bucketName = "aws-file-management";

    @Autowired
    public FileStorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(byte[] bytes, String filename) {
        PutObjectResponse putObjectResponse = null;

        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            putObjectResponse =
                    s3Client.putObject(
                            putObjectRequest,
                            RequestBody.fromInputStream(inputStream, bytes.length)
                    );

        } catch (S3Exception e) {
            throw new RuntimeException(e);
        } finally {
            return putObjectResponse.eTag();
        }
    }


    @Override
    public void deleteFile(String targetETag) {
        List<S3Object> summaries = getListFiles();
        String targetKey = null;

        for (S3Object objectSummary : summaries) {
            // Check if ETag matches
            if (objectSummary.eTag().equals(targetETag)) {
                targetKey = objectSummary.key();
            }
        }

        if (targetKey == null) {
            throw new IllegalArgumentException("eTag does not exist");
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(targetKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFilePath(String targetETag) {
        List<S3Object> summaries = getListFiles();
        String targetKey = null;

        for (S3Object objectSummary : summaries) {
            // Check if ETag matches
            if (objectSummary.eTag().equals(targetETag)) {

                targetKey = objectSummary.key();
            }
        }

        if (targetKey == null) {
            throw new IllegalArgumentException("eTag does not exist");
        }

        return "s3://" + bucketName + "/" + targetKey;
    }

    private List<S3Object> getListFiles() {
        List<S3Object> summaries = null;

        try {
            // List objects in the bucket
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            summaries = res.contents();

        } catch (S3Exception e) {
            throw new RuntimeException(e);
        }
        return summaries;
    }
    @Override
    public List<String> listFiles() {
        List<S3Object> summaries = null;

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            summaries = res.contents();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return summaries.stream().map(S3Object::eTag).collect(Collectors.toList());
    }


}
