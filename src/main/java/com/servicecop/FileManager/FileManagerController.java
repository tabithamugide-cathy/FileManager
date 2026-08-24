package com.servicecop.FileManager;

import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FileManagerController {
    FileStorageService fileStorageService;
    private static final Logger log = Logger.getLogger(FileManagerController.class.getName());

    public FileManagerController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload-file")
    public boolean uploadFile(@RequestParam("file") MultipartFile file) {
        try{
            fileStorageService.saveFile(file);
        return true;
        }
        catch(IOException e){
            log.log(Level.SEVERE, "Exception during upload", e);
             }
        return false;
        }

        @GetMapping("/download-file")
    public ResponseEntity<InputStreamResource>downloadedFile(@RequestParam("fileName") String filename) {
       try {
           var fileToDownload = fileStorageService.getDownloadedFile(filename);

           return ResponseEntity.ok()
                   //.headers(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ filename +"\")
                   .contentLength(fileToDownload.length())
                   .contentType(MediaType.APPLICATION_OCTET_STREAM)
                   .body (new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
       }catch (Exception e){
           return ResponseEntity.notFound().build();
       }
        }
    }

