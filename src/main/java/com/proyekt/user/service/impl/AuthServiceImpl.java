package com.proyekt.user.service.impl;

import com.proyekt.user.dto.AuthResponse;
import com.proyekt.user.dto.DtoLoginUser;
import com.proyekt.user.dto.DtoRegisterUser;
import com.proyekt.user.dto.RefreshToken;
import com.proyekt.user.exception.BaseException;
import com.proyekt.user.exception.ErrorMessage;
import com.proyekt.user.exception.MessageType;
import com.proyekt.user.jwt.JwtService;
import com.proyekt.user.model.RefreshTokenRequest;
import com.proyekt.user.model.User;
import com.proyekt.user.repository.RepositoryUser;
import com.proyekt.user.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private RepositoryUser repositoryUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationProvider authenticationProvider;


    @Override
    public AuthResponse saveRegisterUser(DtoRegisterUser dtoRegisterUser) {

        // İlk öncə istifadəçinin artıq mövcud olub-olmadığını yoxlayın

       Optional<User> optionalUser = repositoryUser.findByUserName(dtoRegisterUser.getUserName());

        if (optionalUser.isPresent()) {
            throw new BaseException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS,null));
        }

        User user = new User();

        AuthResponse authResponse = new AuthResponse();

        user.setFirstName(dtoRegisterUser.getFirstName());
        user.setLastName(dtoRegisterUser.getLastName());
        user.setUserName(dtoRegisterUser.getUserName());
        user.setEmail(dtoRegisterUser.getEmail());
        user.setProfileImageUrl(dtoRegisterUser.getImageUser());
        user.setPassword(passwordEncoder.encode(dtoRegisterUser.getPassword()));
        User saveduser = repositoryUser.save(user);

        String accessToken = jwtService.generateToken(saveduser);

        String refreshToken = jwtService.refreshTokenGenered(saveduser);

        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);


        return authResponse;
    }

    @Override
    public AuthResponse loginUser(DtoLoginUser dtoLoginUser) {

        AuthResponse authResponse = new AuthResponse();

        try {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            dtoLoginUser.getUserName(),  // ← "ali2026"
                            dtoLoginUser.getPassword()   // ← "123456"
                    );

            authenticationProvider.authenticate(auth);

            Optional<User> userDB = repositoryUser.findByUserName(dtoLoginUser.getUserName());

            String accessToken = jwtService.generateToken(userDB.get());

            String refreshToken = jwtService.refreshTokenGenered(userDB.get());


            return new AuthResponse(accessToken, refreshToken);

        } catch (BaseException baseException) {
           throw new BaseException(new ErrorMessage(MessageType.INVALID_CREDENTIALS,"yeniden yoxlayin"));
        }
    }

    @Override
    public RefreshToken accessTokenSend(RefreshTokenRequest request) {

        RefreshToken newAccessToken = new RefreshToken();

        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)){

            throw new BaseException(new ErrorMessage(MessageType.INVALID_REFRESH_TOKEN,null)); //EX(1)
        }

        if(jwtService.isTokenRefreshExpired(refreshToken)){

            throw new BaseException(new ErrorMessage(MessageType.TOKEN_EXPIRED,null));//EX(2)
        }

        String userName = jwtService.getUserNameByTokenRefresh(refreshToken);

        Optional<User> userDB = repositoryUser.findByUserName(userName);

        String accessToken = jwtService.generateToken(userDB.get());

        newAccessToken.setAccessToken(accessToken);


        return newAccessToken;
    }
}
