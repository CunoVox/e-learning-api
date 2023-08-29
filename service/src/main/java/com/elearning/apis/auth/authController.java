package com.elearning.apis.auth;

import com.elearning.controller.UserController;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.UserFormDTO;
import com.elearning.handler.ServiceException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class authController {
    @Autowired
    UserController userController;

    @PostMapping("/register")
    public UserDTO register(@Valid @RequestBody UserFormDTO userFormDTO) throws ServiceException {
        UserDTO dto = userController.register(userFormDTO);
        return dto;
    }
}
