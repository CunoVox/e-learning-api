package com.elearning.controller;

import com.elearning.controller.impl.UserController;
import com.elearning.dtos.UserDTO;
import com.elearning.dtos.UserFormDTO;
import com.elearning.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan({"com.elearning"})
public class Tests {
    @Autowired
    UserController userController;
    @Test
    public void testss1(){
        UserDTO user = userController.register(new UserFormDTO());
        Assertions.assertNotNull(user);

    }
}
