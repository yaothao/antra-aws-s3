package com.example.awss3.controller;

import com.example.awss3.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @Autowired
    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        byte[] bytes = multipartFile.getBytes();
        String fileName = multipartFile.getOriginalFilename();
        return new ResponseEntity<>("File eTag : " + fileStorageService.uploadFile(bytes, fileName), HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("file-eTag") String eTag) {
        fileStorageService.deleteFile("\"" + eTag + "\"");
        return new ResponseEntity<>("File delete successful", HttpStatus.OK);
    }

    @GetMapping("/file-list")
    public ResponseEntity<?> getListFile() {
        return new ResponseEntity<>(fileStorageService.listFiles(), HttpStatus.OK);
    }

    @GetMapping("/file-path")
    public ResponseEntity<?> getFilePath(@RequestParam("file-eTag") String eTag) {
        return new ResponseEntity<>("File Path : " + fileStorageService.getFilePath("\"" + eTag + "\""), HttpStatus.OK);
    }

}
