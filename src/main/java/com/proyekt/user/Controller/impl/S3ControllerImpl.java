package com.proyekt.user.Controller.impl;

import com.proyekt.user.Controller.IS3Controller;
import com.proyekt.user.service.IS3Service;
import com.proyekt.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/s3")
public class S3ControllerImpl implements IS3Controller {

    @Autowired
    private IS3Service is3Service;

    @Autowired
    private IUserService userService;

    // Spring avtomatik S3Service-i tapıb buraya verir (Constructor Injection)
    // Əl ilə "new S3Service()" yazmağa ehtiyac yoxdur

    // POST /api/s3/upload — Şəkil yükləmə endpoint-i
    // @RequestParam("file") — Postman-dan və ya frontend-dən göndərilən faylı alır
    // MultipartFile — Spring-in fayl tipidir (şəkil, video, sənəd və s.)

    @Override
    @PostMapping(path = "/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Faylı S3-ə yükləyir və URL-ini alır
            // Məsələn: https://user-app-images.s3.eu-north-1.amazonaws.com/a3f5d8e1-foto.jpg
            String fileUrl = is3Service.uploadFile(file);

            // 200 OK cavabı ilə URL-i qaytarır
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            // Xəta olarsa 500 qaytarır
            return ResponseEntity.status(500).body("Fayl yüklənmədi: " + e.getMessage());
        }
    }

    @Override
    @PostMapping(path = "/upload/{userId}")
    public ResponseEntity<String> uploadFile(@PathVariable(name = "userId") Long userId,
                                             @RequestParam("file") MultipartFile file) {

        try {
            // 1. Faylı S3-ə yükləyir və URL-ini alır
            // Məsələn: https://user-app-images.s3.eu-north-1.amazonaws.com/abc123-foto.jpg
            String fileUrl = is3Service.uploadFile(file);

            // 2. Həmin URL-i DB-dəki istifadəçinin profileImageUrl field-inə yazır
            // Yəni artıq DB-də null yox, şəklin URL-i olacaq
            userService.updateProfileImage(userId, fileUrl);

            // 3. Uğurlu cavab qaytarır (200 OK + URL)
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            // Xəta olarsa 500 qaytarır
            return ResponseEntity.status(500).body("Fayl yüklənmədi: " + e.getMessage());
        }

    }
}

