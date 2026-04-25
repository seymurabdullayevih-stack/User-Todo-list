package com.proyekt.user.service;

import com.proyekt.user.dto.AuthResponse;
import com.proyekt.user.dto.DtoLoginUser;
import com.proyekt.user.dto.DtoRegisterUser;
import com.proyekt.user.dto.RefreshToken;
import com.proyekt.user.model.RefreshTokenRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthService {

    public AuthResponse saveRegisterUser(DtoRegisterUser dtoRegisterUser);

    public AuthResponse loginUser(DtoLoginUser dtoLoginUser);

    public RefreshToken accessTokenSend(RefreshTokenRequest request);
}
