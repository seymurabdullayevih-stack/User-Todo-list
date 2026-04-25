package com.proyekt.user.Controller.impl;

import com.proyekt.user.Controller.IAuthController;
import com.proyekt.user.dto.AuthResponse;
import com.proyekt.user.dto.DtoLoginUser;
import com.proyekt.user.dto.DtoRegisterUser;
import com.proyekt.user.dto.RefreshToken;
import com.proyekt.user.model.RefreshTokenRequest;
import com.proyekt.user.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/auth")
public class AuthControllerImpl implements IAuthController {

    @Autowired
    private IAuthService authServicel;

    @Override
    @PostMapping(path = "/register")
    public AuthResponse saveRegisterUser(@RequestBody DtoRegisterUser dtoRegisterUser) {

        return authServicel.saveRegisterUser(dtoRegisterUser);
    }

    @Override
    @PostMapping(path = "/authenticate")
    public AuthResponse loginUser(@RequestBody DtoLoginUser dtoLoginUser) {
        return authServicel.loginUser(dtoLoginUser);
    }

    @Override
    @PostMapping(path = "/refresh")
    public RefreshToken accessTokenSend(@RequestBody RefreshTokenRequest request) {

        return authServicel.accessTokenSend(request);
    }


}
