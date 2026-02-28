package com.proyekt.user.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Controller {

    public ResponseEntity<String> uploadFile(MultipartFile file);


    public ResponseEntity<String> uploadFile( Long userId,MultipartFile file);
}
