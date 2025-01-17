package com.example.KhachSan.controller.resource;
import com.example.KhachSan.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/image")
public class FileResource {
    @Autowired
    private IFileService service;

    @PostMapping("/storage")
    public ResponseEntity<?> storageImage(@RequestParam("image") MultipartFile file) throws Exception {
        return ResponseEntity.ok(service.storageImage(file));
    }
}
