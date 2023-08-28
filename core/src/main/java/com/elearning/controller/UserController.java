package com.elearning.controller;

import com.elearning.dtos.UserDTO;
import com.elearning.dtos.UserFormDTO;
import com.elearning.entities.Role;
import com.elearning.entities.User;
import com.elearning.handler.ServiceException;
import com.elearning.reprositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserController {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    IUserRepository userRepository;
    public UserDTO create(UserDTO dto) {
        dto.id = UUID.randomUUID().toString();

        User user = dtoToUser(dto);
        user.roles.add(Role.User);
        user = userRepository.save(user);

        dto = userToDto(user);
        return dto;
    }
    public UserDTO register(UserFormDTO formDto) throws ServiceException {
        User entity = userRepository.findByEmail(formDto.email);
        if(entity == null){
            if(formDto.password.length() < 8)
                throw new ServiceException("Mật khẩu phải có 8 kí tự trở lên");
            UserDTO dto = modelMapper.map(formDto, UserDTO.class);
            dto = create(dto);
            return dto;
        }
        throw new ServiceException("Email đã tồn tại");
    }

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
