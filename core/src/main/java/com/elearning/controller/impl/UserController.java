package com.elearning.controller.impl;

import com.elearning.controller.IUserController;
import com.elearning.dtos.UserDTO;
import com.elearning.dtos.UserFormDTO;
import com.elearning.entities.Role;
import com.elearning.entities.User;
import com.elearning.reprositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserController implements IUserController {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    IUserRepository userRepository;
    @Override
    public UserDTO create(UserDTO dto) {
        dto.id = UUID.randomUUID().toString();

        User user = dtoToUser(dto);
        user.roles.add(Role.User);
        user = userRepository.save(user);

        dto = userToDto(user);
        return dto;
    }
    @Override
    public UserDTO register(UserFormDTO formDto) throws Exception {
        User entity = userRepository.findByEmail(formDto.email);
        if(entity == null){
            if(formDto.password.length() < 8)
                throw new Exception(new Exception("Mật khẩu phải có 8 kí tự trở lên"));
            UserDTO dto = modelMapper.map(formDto, UserDTO.class);
            dto = create(dto);
            return dto;
        }
        throw new Exception(new Exception("Email đã tồn tại"));
    }

    @Override
    public UserDTO login(UserFormDTO dto) {
        return null;
    }

    public User dtoToUser(UserDTO dto){
        User user = modelMapper.map(dto, User.class);
//        User user = User.builder()
//                .id(dto.id)
//                .email(dto.email)
//                .password(dto.password)
//                .fullName(dto.fullName)
//                .address(dto.address)
//                .build();
        return user;
    }
    public UserDTO userToDto(User user){
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        return dto;
    }
}
