package com.proyekt.user.service.impl;

import com.proyekt.user.service.IS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3ServiceImpl implements IS3Service {

    private final S3Client s3Client; // S3-ə qoşulmaq üçün client (S3Config-dən gəlir)

    @Value("${aws.s3.bucket-name}")
    private String bucketName; // application.properties-dən bucket adını oxuyur (user-app-images)

    @Value("${aws.region}")
    private String region; // application.properties-dən regionu oxuyur (eu-north-1)

    // Constructor injection — Spring avtomatik S3Client-i buraya verir
    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // Faylı S3-ə yükləyib URL qaytaran metod

    public String uploadFile(MultipartFile file) throws IOException {

        // Fayla unikal ad verir ki, eyni adlı fayllar bir-birini əzməsin
        // Məsələn: "a3f5d8e1-foto.jpg"
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // S3-ə göndəriləcək sorğunu hazırlayır
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)              // Hansı bucket-ə yükləyəcək
                .key(fileName)                    // Faylın S3-dəki adı
                .contentType(file.getContentType()) // Faylın tipi (image/jpeg, image/png və s.)
                .build();

        // Faylı S3-ə yükləyir
        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        // Yüklənmiş faylın public URL-ini qaytarır
        // Məsələn: https://user-app-images.s3.eu-north-1.amazonaws.com/a3f5d8e1-foto.jpg
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }


}
