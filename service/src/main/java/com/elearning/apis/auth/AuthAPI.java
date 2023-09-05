package com.elearning.apis.auth;

import com.elearning.controller.JwtController;
import com.elearning.controller.RefreshTokenController;
import com.elearning.controller.UserController;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.UserFormDTO;
import com.elearning.models.dtos.auth.AuthResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.elearning.utils.Constants.REFRESH_TOKEN_COOKIE_NAME;
import static com.elearning.utils.Constants.REFRESH_TOKEN_EXPIRE_TIME_MILLIS;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthAPI {
    private final UserController userController;
    private final JwtController jwtController;
    @Autowired
    private RefreshTokenController refreshTokenController;

    public AuthAPI(UserController userController, JwtController jwtController) {
        this.userController = userController;
        this.jwtController = jwtController;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserFormDTO userFormDTO) throws ServiceException {
        var authResponse = userController.register(userFormDTO);
        var cookie = createRefreshCookie(authResponse.getRefreshToken());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserFormDTO userFormDTO) throws ServiceException {
        AuthResponse rs = userController.login(userFormDTO);
        var cookie = createRefreshCookie(rs.getRefreshToken());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(rs);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        Cookie requestCookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
        AuthResponse authResponse = jwtController.refreshToken(requestCookie);
        var cookie = createRefreshCookie(authResponse.getRefreshToken());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse);
    }
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request){
//        Cookie requestCookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
//        refreshTokenController.deleteRefreshTokenBranch(requestCookie.getValue());
//        ResponseCookie delete = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, null).maxAge(0).httpOnly(true).build();
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, delete.toString())
//                .body(null);
//    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME_MILLIS / 1000)
                .path("/")
                .httpOnly(true).build();
    }

}
