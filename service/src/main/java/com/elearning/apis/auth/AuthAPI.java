package com.elearning.apis.auth;

import com.elearning.controller.JwtController;
import com.elearning.controller.RefreshTokenController;
import com.elearning.controller.UserController;
import com.elearning.controller.VerificationCodeController;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.UserEmailRequest;
import com.elearning.models.dtos.auth.UserLoginDTO;
import com.elearning.models.dtos.auth.UserRegisterDTO;
import com.elearning.models.dtos.auth.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.elearning.utils.Constants.REFRESH_TOKEN_COOKIE_NAME;
import static com.elearning.utils.Constants.REFRESH_TOKEN_EXPIRE_TIME_MILLIS;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthAPI {
    private final UserController userController;
    @Autowired
    private VerificationCodeController verificationCodeController;
    private final JwtController jwtController;
    @Autowired
    private RefreshTokenController refreshTokenController;

    public AuthAPI(UserController userController, JwtController jwtController) {
        this.userController = userController;
        this.jwtController = jwtController;
    }

    @Operation(summary = "Gửi mã xác nhận email")
    @PostMapping("/email/verify")
    public ResponseEntity<?> sendEmailVerification(@RequestBody UserEmailRequest request) {
        return ResponseEntity.ok().body(verificationCodeController.createEmailConfirmCode(request.getEmail()));
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userFormDTO) throws ServiceException {
        var authResponse = userController.register(userFormDTO);
        return ResponseEntity
                .ok()
                .body(authResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userFormDTO) throws ServiceException {
        AuthResponse rs = userController.login(userFormDTO);
        return ResponseEntity
                .ok()
                .body(rs);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam("refresh_token") String refreshToken, HttpServletRequest request) {
        AuthResponse authResponse = jwtController.refreshToken(refreshToken);
        return ResponseEntity
                .ok()
                .body(authResponse);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("refresh_token") String refreshToken){
        SecurityContextHolder.clearContext();
        refreshTokenController.deleteRefreshTokenBranch(refreshToken);

        return ResponseEntity.ok()
                .body(null);
    }
}
