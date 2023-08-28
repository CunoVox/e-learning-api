package com.elearning.controller;

import com.elearning.dtos.UserDTO;
import com.elearning.dtos.UserFormDTO;

public interface IUserController {
    UserDTO create(UserDTO dto);
    UserDTO register(UserFormDTO dto) throws Exception;
    UserDTO login(UserFormDTO dto);
}
