package com.elearning.apis.user;

import com.elearning.controller.UserController;
import com.elearning.controller.VerificationCodeController;
import com.elearning.models.dtos.ResetPasswordDTO;
import com.elearning.models.dtos.UpdateUserDTO;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.UserEmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserAPI {
    @Autowired
    private UserController userController;
    private final VerificationCodeController verificationCodeController;
    @PostMapping("/profile/update")
    public UserDTO update(@RequestBody UpdateUserDTO dto){
        var context = SecurityContextHolder.getContext().getAuthentication();
        var email = context.getName();

        return userController.update(email, dto);
    }


//    @Operation(summary = "Xác nhận email")
//    @GetMapping("/email/verify/{user_id}/{token}")
//    public void emailConfirm(@PathVariable("user_id") String userId,
//                             @PathVariable("token") String token) {
//        verificationCodeController.emailConfirmCode(userId, token);
//    }

    @Operation(summary = "Xin gửi mail reset password")
    @PostMapping(value = "/password/reset")
    public ResponseEntity<?> sendEmailResetPassword(@RequestBody @Valid UserEmailRequest request) {
        return ResponseEntity.ok().body(verificationCodeController.createResetPasswordCode(request.getEmail()));
    }

    @Operation(summary = "Kiểm tra reset password code")
    @PostMapping("/password/reset/{user_id}")
    public void resetPasswordCodeConfirm(@PathVariable("user_id") String user_id,
                                         @RequestBody String code) {
        verificationCodeController.resetPasswordConfirmCode(user_id, code);
    }

    @PatchMapping(value = "/password/reset/{user_id}")
    public void resetPassword(@PathVariable("user_id") String user_id,
                              @RequestBody ResetPasswordDTO dto) {
        userController.userResetPassword(user_id, dto);
    }

}
