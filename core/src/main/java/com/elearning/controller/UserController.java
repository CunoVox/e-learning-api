package com.elearning.controller;

import com.elearning.models.dtos.auth.AuthResponse;
import com.elearning.security.SecurityUserDetail;
import com.elearning.utils.EnumRole;
import com.elearning.entities.User;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.UserFormDTO;
import com.elearning.reprositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final IUserRepository userRepository;
    @Autowired
    private final JwtController jwtController;
    @Autowired
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createDTO(UserDTO dto) {
        dto.id = UUID.randomUUID().toString();

        User user = dtoToUser(dto);
        user.roles.add(EnumRole.User);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        dto = userToDto(user);
        return dto;
    }
    public User create(UserDTO dto) {
        dto.id = UUID.randomUUID().toString();
        User user = dtoToUser(dto);
        user.roles.add(EnumRole.User);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        return user;
    }
    public AuthResponse register(UserFormDTO userFormDTO) throws ServiceException{
        UserDTO dto = new UserDTO();
        if(userFormDTO != null){
            User entity = userRepository.findByEmail(userFormDTO.getEmail());
            if (entity != null) {
                throw new ServiceException("Email đã tồn tại");
            }
            if (!isValidEmail(userFormDTO.email)) {
                throw new ServiceException("Email không hợp lệ");
            }
            if (userFormDTO.password.length() < 8)
                throw new ServiceException("Mật khẩu phải có 8 kí tự trở lên");

            dto = modelMapper.map(userFormDTO, UserDTO.class);
            entity = create(dto);
            dto = userToDto(entity);
            UserDetails userDetail =  userDetailsService.loadUserByUsername(entity.email);
            var jwtToken = jwtController.generateToken(userDetail);
            return AuthResponse.builder()
                    .token(jwtToken)
                    .user(dto)
                    .build();
        }
        return new AuthResponse();
    }
    public AuthResponse login(UserFormDTO formDTO) throws ServiceException{
        UserDTO dto = new UserDTO();
        if(formDTO != null){
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            formDTO.getEmail(),
                            formDTO.getPassword()
                    )
            );
            var user = userRepository.findByEmail(formDTO.email);
            dto = userToDto(user);
            UserDetails userDetail =  userDetailsService.loadUserByUsername(user.email);
            var jwtToken = jwtController.generateToken(userDetail);
            return AuthResponse.builder()
                    .token(jwtToken)
                    .user(dto)
                    .build();
        }
        return new AuthResponse();
    }
//    public UserDTO login(UserFormDTO formDto) throws ServiceException {
//        UserDTO dto = new UserDTO();
//        if(formDto != null){
//            User entity = userRepository.findByEmail(formDto.getEmail());
//            if(entity == null){
//                throw new ServiceException("Email không tồn tại");
//            }
//            if(!passwordEncoder.matches(formDto.password, entity.password)){
//                throw new ServiceException("Mật khẩu không đúng");
//            }
//            if(entity.isDeleted){
//                throw new ServiceException("Tài khoản bị tạm khóa");
//            }
//            dto = userToDto(entity);
//        }
//        return dto;
//    }
    public UserDTO findByEmail(String email){
        User user = userRepository.findByEmail(email);
        return userToDto(user);
    }
    public boolean isValidEmail(String email) {
        boolean result = true;
        result = EmailValidator.getInstance()
                .isValid(email);
        return result;
    }

    public User dtoToUser(UserDTO dto) {
        User user = modelMapper.map(dto, User.class);
        return user;
    }

    public UserDTO userToDto(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        return dto;
    }

}
