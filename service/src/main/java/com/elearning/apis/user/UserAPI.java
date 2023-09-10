package com.elearning.apis.user;

import com.elearning.controller.VerificationCodeController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserAPI {
    private final VerificationCodeController verificationCodeController;
    @GetMapping("/email/verify/{userId}")
    public ResponseEntity<?> sendEmail(@PathVariable("userId") String userId){
        return ResponseEntity.ok().body(verificationCodeController.reCreateEmailConfirmCode(userId));
    }
    @GetMapping("/email/verify/{userId}")
    public ResponseEntity<?> emailConfirm(@PathVariable("userId") String userId,
                                          @RequestParam("token") String token) {
        verificationCodeController.emailConfirmCode(token);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/password/")
    public ResponseEntity<?> resetPassword(@RequestBody String email){
        return ResponseEntity.ok().body(verificationCodeController.reCreateResetPasswordCode(email));
    }
}
