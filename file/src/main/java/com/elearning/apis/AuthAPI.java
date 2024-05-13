package com.elearning.apis;

import com.elearning.controller.AuthController;
import com.elearning.models.wrapper.ObjectResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthAPI {
    @Autowired
    private AuthController authController;

    @GetMapping("/url")
    public String getAuthUrl() {
        return authController.getAuthUrl();
    }

    @GetMapping("/token")
    public ObjectResponseWrapper createToken(@RequestParam String code) {
        return authController.createToken(code);
    }

    @GetMapping("/refresh-token")
    public ObjectResponseWrapper refreshToken() {
        authController.refreshToken();
        return ObjectResponseWrapper.builder().status(1).build();
    }

}
