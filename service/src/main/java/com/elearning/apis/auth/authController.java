package com.elearning.apis.auth;

import com.elearning.controller.IUserController;
import com.elearning.dtos.UserDTO;
import com.elearning.dtos.UserFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class authController {
    @Autowired
    IUserController userController;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserFormDTO userFormDTO) throws Exception {
        UserDTO dto = userController.register(userFormDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
