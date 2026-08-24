package com.servicecop.FileManager;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    private final String STORAGE_DIRECTORY = "/home/mugide-tabitha/Documents/Storage";
    public void saveFile(MultipartFile fileSave) throws IOException {

        if (fileSave == null){
            throw new NullPointerException("file to save is null");
        }
        var targetFile = new File(STORAGE_DIRECTORY + File.separator + fileSave.getOriginalFilename());
        if(!Objects.equals(targetFile.getParent(), STORAGE_DIRECTORY)){
            throw new SecurityException("Unsupported filename!");
        }
        Files.copy(fileSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public File getDownloadedFile(String fileName) throws Exception{
        if (fileName == null){
            throw new NullPointerException("fileName is null");
        }
        var fileToDownload = new File(STORAGE_DIRECTORY + File.separator + fileName);
        if (!Objects.equals(fileToDownload.getParent(), STORAGE_DIRECTORY)){
            throw new SecurityException("Unsupported filename!");
        }
        if (!fileToDownload.exists()){
            throw new FileNotFoundException("No file named:" +fileName);
        }
        return fileToDownload;
    }
}
