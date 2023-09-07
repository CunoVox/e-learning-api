package com.elearning.controller;

import com.elearning.entities.User;
import com.elearning.entities.VerificationCode;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.auth.UserLoginDTO;
import com.elearning.models.dtos.auth.UserRegisterDTO;
import com.elearning.models.dtos.auth.AuthResponse;
import com.elearning.reprositories.IUserRepository;
import com.elearning.security.SecurityUserDetail;
import com.elearning.utils.enumAttribute.EnumRole;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumVerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.validator.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.elearning.utils.Extensions.toList;

@Service
@RequiredArgsConstructor
@ExtensionMethod(Extensions.class)
public class UserController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private JwtController jwtController;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private VerificationCodeController verificationCodeController;

    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createDTO(UserDTO dto) {
        dto.setId(UUID.randomUUID().toString());

        User user = dtoToUser(dto);
        user.roles.add(EnumRole.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        dto = userToDto(user);
        return dto;
    }

    public User create(UserDTO dto) {
        dto.setId(UUID.randomUUID().toString());
        User user = dtoToUser(dto);
        user.setEmail(user.getEmail().trim().toLowerCase(Locale.ROOT));
        user.roles.add(EnumRole.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        return user;
    }

    public AuthResponse register(UserRegisterDTO userFormDTO) throws ServiceException {
        UserDTO dto;
        if (userFormDTO != null) {
            User entity = userRepository.findByEmail(userFormDTO.getEmail());
            if (entity != null) {
                throw new ServiceException("Email đã tồn tại");
            }
            if (!isValidEmail(userFormDTO.getEmail())) {
                throw new ServiceException("Email không hợp lệ");
            }
            if (userFormDTO.getPassword().length() < 8)
                throw new ServiceException("Mật khẩu phải có 8 kí tự trở lên");

            dto = modelMapper.map(userFormDTO, UserDTO.class);
            entity = create(dto);

            verificationCodeController.revokeAllUserVerificationCode(entity.getId());
            VerificationCode code = verificationCodeController.build(entity.getId(),null, EnumVerificationCode.EMAIL_CONFIRM);
            verificationCodeController.create(code);

            return getAuthResponse(entity);
        }
        return new AuthResponse();
    }

    public AuthResponse login(UserLoginDTO formDTO) throws ServiceException {
        if (formDTO != null) {
            User entity = userRepository.findByEmail(formDTO.getEmail());
            if(entity == null){
                throw new ServiceException("Email không tồn tại");
            }
            if(!passwordEncoder.matches(formDTO.getPassword(), entity.getPassword())){
                throw new ServiceException("Mật khẩu không đúng");
            }
            if(entity.getIsDeleted()){
                throw new ServiceException("Tài khoản bị tạm khóa");
            }
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            formDTO.getEmail(),
                            formDTO.getPassword()
                    )
            );
//            var user = entity;
            return getAuthResponse(entity);
        }
        return new AuthResponse();
    }

    private AuthResponse getAuthResponse(User entity) {
        UserDTO dto;
        dto = userToDto(entity);
        SecurityUserDetail userDetail = (SecurityUserDetail) userDetailsService.loadUserByUsername(entity.getEmail());
        var jwtToken = jwtController.generateToken(userDetail);
        var refreshToken = jwtController.generateRefreshToken(userDetail);
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .user(dto)
                .build();
    }

    public List<User> findAllUser(){
        return userRepository.findAll();
    }
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return userToDto(user);
    }
    public UserDTO findById(String id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new ServiceException("Không tìm thấy người dùng");
        }
        return userToDto(user.get());
    }
    public boolean isValidEmail(String email) {
        boolean result;
        result = EmailValidator.getInstance()
                .isValid(email);
        return result;
    }

    @Transactional
    public void userEmailConfirmed(String id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            user.get().setIsEmailConfirmed(true);
            user.get().setUpdatedAt(new Date());
            userRepository.save(user.get());
        }
    }
    public User dtoToUser(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public UserDTO userToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

}
