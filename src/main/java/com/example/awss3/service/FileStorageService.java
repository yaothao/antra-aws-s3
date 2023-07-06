package com.example.awss3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@Service
public interface FileStorageService {

    String uploadFile(byte[] bytes, String filename);

    void deleteFile(String eTag);

    public String getFilePath(String targetETag);

    List<String> listFiles();

}
