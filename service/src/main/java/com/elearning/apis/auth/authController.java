package com.elearning.apis.auth;

import com.elearning.controller.UserController;
import com.elearning.dtos.UserDTO;
import com.elearning.dtos.UserFormDTO;
import com.elearning.handler.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class authController {
    @Autowired
    UserController userController;

    @PostMapping("/register")
    public UserDTO register(@Valid @RequestBody UserFormDTO userFormDTO) throws ServiceException {
        UserDTO dto = userController.register(userFormDTO);
        return dto;
    }
}
