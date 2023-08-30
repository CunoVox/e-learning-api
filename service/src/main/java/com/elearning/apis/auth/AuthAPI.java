package com.elearning.apis.auth;

import com.elearning.controller.UserController;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.UserFormDTO;
import com.elearning.models.dtos.auth.AuthResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.elearning.utils.Constants.REFRESH_TOKEN_EXPIRE_TIME_MILLIS;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthAPI {
    private final UserController userController;
    public AuthAPI(UserController userController) {
        this.userController = userController;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody UserFormDTO userFormDTO) throws ServiceException {
        var authResponse = userController.register(userFormDTO);
//        var cookie = createRefreshCookie(authResponse.getRefreshToken());
//        ResponseEntity
//                .ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .build();
        return authResponse;
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
    private ResponseCookie createRefreshCookie(String refreshToken){
        return ResponseCookie.from("X-REFRESH-TOKEN", refreshToken)
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME_MILLIS / 1000)
                .httpOnly(true)
                .path("/")
                .httpOnly(true).build();
    }

}
