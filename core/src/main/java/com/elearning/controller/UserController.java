package com.elearning.controller;

import com.elearning.email.EmailSender;
import com.elearning.entities.User;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.*;
import com.elearning.models.dtos.auth.AuthResponse;
import com.elearning.models.dtos.auth.UserLoginDTO;
import com.elearning.models.dtos.auth.UserRegisterDTO;
import com.elearning.models.searchs.ParameterSearchUser;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.reprositories.IUserRepository;
import com.elearning.security.SecurityUserDetail;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumParentFileType;
import com.elearning.utils.enumAttribute.EnumRole;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.validator.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ExtensionMethod(Extensions.class)
public class UserController extends BaseController{
    private final ModelMapper modelMapper;
    private final IUserRepository userRepository;
    private final JwtController jwtController;
    private final UserDetailsService userDetailsService;
    @Autowired
    private VerificationCodeController verificationCodeController;
    @Autowired
    private FileRelationshipController fileRelationshipController;
    @Autowired
    private EmailSender emailSender;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;

    public UserDTO createDTO(UserDTO dto) {
        dto.setId(sequenceValueItemRepository.getSequence(User.class));

        User user = dtoToUser(dto);
        user.roles.add(EnumRole.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFullNameMod(StringUtils.stripAccents(user.getFullName()));
        user = userRepository.save(user);
        dto = toDto(user);
        return dto;
    }

    public ListWrapper<UserDTO> searchUser(ParameterSearchUser parameterSearchUser) {
        ListWrapper<User> wrapper = userRepository.searchUser(parameterSearchUser);
        List<UserDTO> userDTOS = toDTOs(wrapper.getData());
        return ListWrapper.<UserDTO>builder()
                .currentPage(wrapper.getCurrentPage())
                .totalPage(wrapper.getTotalPage())
                .maxResult(wrapper.getMaxResult())
                .total(wrapper.getTotal())
                .data(userDTOS)
                .build();
    }

    public void lockAndUnLockUser(String userId, boolean lock) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ServiceException("Không tìm thấy người dùng trong hệ thống!");
        }
        userRepository.updateDeleted(userId, lock, getUserIdFromContext());
    }

    public UserDTO getUserDetail(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ServiceException("Không tìm thấy người dùng trong hệ thống!");
        }
        return toDto(user.get());
    }

    public User create(UserDTO dto) {
        dto.setId(sequenceValueItemRepository.getSequence(User.class));
        User user = dtoToUser(dto);
        user.setEmail(user.getEmail().trim().toLowerCase(Locale.ROOT));
        user.roles.add(EnumRole.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFullNameMod(StringUtils.stripAccents(user.getFullName()));
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
            if (userFormDTO.getPassword().trim().length() < 8)
                throw new ServiceException("Mật khẩu phải có 8 kí tự trở lên");

            verificationCodeController.emailConfirmCode(userFormDTO.getEmail(), userFormDTO.getCode());

            dto = modelMapper.map(userFormDTO, UserDTO.class);
            entity = create(dto);
            this.userEmailConfirm(entity.getId());
            verificationCodeController.revokeAllUserEmailVerificationCode(entity.getId());
//            VerificationCode code = verificationCodeController.build(null, userFormDTO.getEmail() , EnumVerificationCode.EMAIL_CONFIRM);
//            emailSender.sendMail(entity.getEmail(), code);
//            verificationCodeController.create(code);

            return getAuthResponse(entity);
        }
        return new AuthResponse();
    }

    public AuthResponse login(UserLoginDTO formDTO) throws ServiceException {
        if (formDTO != null) {
            User entity = userRepository.findByEmail(formDTO.getEmail());
            if (entity == null) {
                throw new ServiceException("Email hoặc mật khẩu không chính xác!");
            }
            if (!passwordEncoder.matches(formDTO.getPassword(), entity.getPassword())) {
                throw new ServiceException("Email hoặc mật khẩu không chính xác!");
            }
            if (entity.getIsDeleted()) {
                throw new ServiceException("Tài khoản bị tạm khóa, vui lòng liên hệ quản trị viên để được hổ trợ!");
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

    public UserDTO update(String email, UpdateUserDTO dto) {
        User entity = userRepository.findByEmail(email);
        if (entity == null) {
            throw new ServiceException("User not found");
        }
        int flag = 0;
        if (!dto.getFullName().isEmpty()) {
            entity.setFullName(dto.getFullName());
            entity.setFullNameMod(StringUtils.stripAccents(entity.getFullName()));
            flag = 1;
        }
        if (!dto.getAddress().isEmpty()) {
            entity.setAddress(dto.getAddress());
            flag = 1;
        }
        if (flag != 0) {
            entity.setUpdatedAt(new Date());
        }
        userRepository.save(entity);
        return toDto(entity);
    }

    private AuthResponse getAuthResponse(User entity) {
        UserDTO dto;
        dto = toDto(entity);
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
    public void userResetPassword(String email, ResetPasswordDTO dto) {
        User u = userRepository.findByEmail(email);
        if (u != null) {
            String userId = u.getId();
            verificationCodeController.resetPasswordConfirmCode(userId, dto.getCode());
            if (dto.getNewPassword().length() < 8) {
                throw new ServiceException("Mật khẩu phải có 8 kí tự trở lên");
            }
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                throw new ServiceException("Mật khẩu xác nhận không chính xác");
            }
            verificationCodeController.confirmCode(userId, dto.getCode());
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                user.get().setUpdatedAt(new Date());
                user.get().setPassword(passwordEncoder.encode(dto.getConfirmPassword()));
                userRepository.save(user.get());
            }
        } else {
            throw new ServiceException("Không tìm thấy người dùng");
        }
    }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ServiceException("Không tìm thấy người dùng");
        }
        return toDto(user);
    }

    public UserDTO findById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ServiceException("Không tìm thấy người dùng");
        }
        return toDto(user.get());
    }

    protected User findUserById(String id) {
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
            if (newPass.length() < 8) {
                throw new ServiceException("Mật khẩu phải có 8 kí tự trở lên");
            }
            if (!newPass.equals(confirmPass)) {
                throw new ServiceException("Mật khẩu xác nhận không đúng");
            }
            User entity = user.get();
            entity.setPassword(newPass);
//            entity.setIsEmailConfirmed(true);
            entity.setUpdatedAt(new Date());
            userRepository.save(entity);
            return toDto(entity);
        }
    }

    public User dtoToUser(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        List<FileRelationshipDTO> fileRelationshipDTO = fileRelationshipController.getFileRelationships(Collections.singletonList(user.getId()), EnumParentFileType.USER_AVATAR.name());
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(!fileRelationshipDTO.isNullOrEmpty() ? fileRelationshipDTO.get(0).getPathFile() : null)
                .address(user.getAddress())
                .isDeleted(user.getIsDeleted())
                .isEmailConfirmed(user.isEmailConfirmed)
                .build();
//        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> toDTOs(List<User> users) {
        if (users.isNullOrEmpty()) return new ArrayList<>();
        List<String> ids = users.stream().map(User::getId).collect(Collectors.toList());
        List<FileRelationshipDTO> fileRelationshipDTOS = fileRelationshipController.getFileRelationships(ids, EnumParentFileType.USER_AVATAR.name());
        Map<String, FileRelationshipDTO> fileRelationshipDTOMap = new HashMap<>();
        for (FileRelationshipDTO fileRelationshipDTO : fileRelationshipDTOS) {
            if (fileRelationshipDTOMap.get(fileRelationshipDTO.getParentId()) != null) {
                fileRelationshipDTOMap.put(fileRelationshipDTO.getParentId(), fileRelationshipDTO);
            }
        }
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            FileRelationshipDTO fileRelationshipDTO = fileRelationshipDTOMap.get(user.getId());
            userDTOS.add(UserDTO.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .avatar(fileRelationshipDTO != null ? fileRelationshipDTO.getPathFile() : null)
                    .address(user.getAddress())
                    .isDeleted(user.getIsDeleted())
                    .isEmailConfirmed(user.isEmailConfirmed)
                    .build());
        }
        return userDTOS;
    }
}
