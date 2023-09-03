package com.elearning.apis.test;

import com.elearning.controller.UserController;
import com.elearning.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/test")
@PreAuthorize("hasRole('ADMIN')")
public class testAPI {

    @Autowired
    private UserController userController;
    @GetMapping("/users")
    public List<User> test(){
        return userController.findAllUser() ;
    }
}
