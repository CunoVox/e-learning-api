package com.elearning.controller;

import com.elearning.email.EmailSender;
import com.elearning.entities.User;
import com.elearning.entities.VerificationCode;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.ResetPasswordDTO;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.auth.AuthResponse;
import com.elearning.models.dtos.auth.UserLoginDTO;
import com.elearning.models.dtos.auth.UserRegisterDTO;
import com.elearning.reprositories.IUserRepository;
import com.elearning.security.SecurityUserDetail;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumRole;
import com.elearning.utils.enumAttribute.EnumVerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.validator.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@ExtensionMethod(Extensions.class)
public class UserController {
    private final ModelMapper modelMapper;
    private final IUserRepository userRepository;
    private final JwtController jwtController;
    private final UserDetailsService userDetailsService;
    @Autowired
    private VerificationCodeController verificationCodeController;
    @Autowired
    private EmailSender emailSender;
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

    @Transactional(rollbackFor = {ServiceException.class, NullPointerException.class})
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

            verificationCodeController.revokeAllUserEmailVerificationCode(entity.getId());
            VerificationCode code = verificationCodeController.build(entity.getId(), null, EnumVerificationCode.EMAIL_CONFIRM);
            emailSender.sendMail(entity.getEmail(), code);
            verificationCodeController.create(code);

            return getAuthResponse(entity);
        }
        return new AuthResponse();
    }

    public AuthResponse login(UserLoginDTO formDTO) throws ServiceException {
        if (formDTO != null) {
            User entity = userRepository.findByEmail(formDTO.getEmail());
            if (entity == null) {
                throw new ServiceException("Email không tồn tại");
            }
            if (!passwordEncoder.matches(formDTO.getPassword(), entity.getPassword())) {
                throw new ServiceException("Mật khẩu không đúng");
            }
            if (entity.getIsDeleted()) {
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

    @Transactional
    public void userEmailConfirm(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setIsEmailConfirmed(true);
            user.get().setUpdatedAt(new Date());
            userRepository.save(user.get());
        }
    }
    @Transactional
    public void userResetPassword(String userId, ResetPasswordDTO dto){
        verificationCodeController.resetPasswordConfirmCode(userId, dto.getCode());
        if(dto.getNewPassword().length() < 8){
            throw new ServiceException("Mật khẩu phải có 8 kí tự trở lên");
        }
        if(!dto.getNewPassword().equals(dto.getConfirmPassword())){
            throw new ServiceException("Mật khẩu xác nhận không chính xác");
        }
        verificationCodeController.confirmCode(userId, dto.getCode());
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()){
            user.get().setUpdatedAt(new Date());
            user.get().setPassword(passwordEncoder.encode(user.get().getPassword()));
        }
    }
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new ServiceException("Không tìm thấy người dùng");
        }
        return userToDto(user);
    }

    public UserDTO findById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ServiceException("Không tìm thấy người dùng");
        }
        return userToDto(user.get());
    }
    protected User findUserById(String id){
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ServiceException("Không tìm thấy người dùng");
        }
        return user.get();
    }
    public boolean isValidEmail(String email) {
        boolean result;
        result = EmailValidator.getInstance()
                .isValid(email);
        return result;
    }


    public UserDTO userResetPassword(String id, String newPass, String confirmPass) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ServiceException("Không tìm thấy người dùng");
        } else {
            if(newPass.length() < 8){
                throw new ServiceException("Mật khẩu phải có 8 kí tự trở lên");
            }
            if(!newPass.equals(confirmPass)){
                throw new ServiceException("Mật khẩu xác nhận không đúng");
            }
            User entity = user.get();
            entity.setPassword(newPass);
//            entity.setIsEmailConfirmed(true);
            entity.setUpdatedAt(new Date());
            userRepository.save(entity);
            return userToDto(entity);
        }
    }

    public User dtoToUser(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public UserDTO userToDto(User user) {
        if(user == null){
            return null;
        }
        return modelMapper.map(user, UserDTO.class);
    }

}
