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
public class UserController extends BaseController {
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
        if (user.get().getRoles().stream().anyMatch(u -> u.equals(EnumRole.ROLE_ADMIN))) {
            if ((new ArrayList<>(getUserDetailFromContext().getAuthorities())).stream().noneMatch(u -> (u.toString()).equals(EnumRole.ROLE_ADMIN.name()))) {
                throw new ServiceException("Không đủ quyền thay đổi trạng thái người dùng này");
            }
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

    public UserDTO update(UserDTO dto) {
        String userId = getUserIdFromContext();
        if (userId == null) {
            throw new ServiceException("Vui lòng đăng nhập.");
        }
        Optional<User> entity = userRepository.findById(userId);
        if (entity.isEmpty()) {
            throw new ServiceException("User not found");
        }
        if (!dto.getFullName().isEmpty()) {
            entity.get().setFullName(dto.getFullName());
            entity.get().setFullNameMod(StringUtils.stripAccents(entity.get().getFullName()));
        }
        if (!dto.getAddress().isEmpty()) {
            entity.get().setAddress(dto.getAddress());
        }
        if (!dto.getPhoneNumber().isBlankOrNull()) {
            entity.get().setPhoneNumber(dto.getPhoneNumber());
        }
        if (!dto.getProfileLink().isBlankOrNull()) {
            entity.get().setProfileLink(dto.getProfileLink());
        }
        if (!dto.getDescription().isBlankOrNull()) {
            entity.get().setDescription(dto.getDescription());
        }
        entity.get().setUpdatedAt(new Date());
        userRepository.save(entity.get());
        return toDto(entity.get());
    }

    public UserDTO userLecturerUpdate(UserDTO dto) {
        String userId = this.getUserIdFromContext();
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ServiceException("Vui lòng đăng nhập");
        }
        User newUser = user.get();
        if (!dto.getPhoneNumber().isBlankOrNull()) {
            newUser.setPhoneNumber(dto.getPhoneNumber());
        }
        if (!dto.getProfileLink().isBlankOrNull()) {
            newUser.setProfileLink(dto.getProfileLink());
        }
        if (!dto.getDescription().isBlankOrNull()) {
            newUser.setDescription(dto.getDescription());
        }
        if (!newUser.getRoles().contains(EnumRole.ROLE_LECTURE)) {
            newUser.getRoles().add(EnumRole.ROLE_LECTURE);
        }
        newUser.setUpdatedAt(new Date());
        userRepository.save(newUser);

        return toDto(newUser);
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

    public void updateRoles(String userId, List<EnumRole> roles) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ServiceException("Không tìm thấy người dùng trong hệ thống!");
        }
        //Manager không được thay đổi quyền của admin và ngang cấp và không được nâng quyền của mình lên cấp cao hơn
        if ((user.get().getRoles().stream().anyMatch(u -> (u.equals(EnumRole.ROLE_ADMIN) || u.equals(EnumRole.ROLE_MANAGER))) ||
                roles.stream().anyMatch(r -> r.equals(EnumRole.ROLE_ADMIN))) &&
                (new ArrayList<>(getUserDetailFromContext().getAuthorities()))
                        .stream().noneMatch(u -> (u.toString()).equals(EnumRole.ROLE_ADMIN.name()))) {
            throw new ServiceException("Không đủ quyền hạn để thay đổi vài trò");
        }
        //nếu rỗng thì set thành quyền user
        if (roles.isNullOrEmpty()) {
            roles = Collections.singletonList(EnumRole.ROLE_USER);
        }
        userRepository.updateUserRoles(userId, roles, getUserIdFromContext());
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

    public Map<String, UserDTO> getUserByIds(List<String> ids) {
        if (ids.isNullOrEmpty()) return new HashMap<>();
        Map<String, UserDTO> map = new HashMap<>();
        ListWrapper<UserDTO> wrapper = searchUser(ParameterSearchUser.builder().userIds(ids).build());
        if (wrapper != null && !wrapper.getData().isNullOrEmpty()) {
            map = wrapper.getData().stream()
                    .filter(u -> ids.contains(u.getId()))
                    .collect(Collectors.toMap(UserDTO::getId, u -> u));
        }
        return map;
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
                .roles(user.getRoles())
                .avatar(!fileRelationshipDTO.isNullOrEmpty() ? fileRelationshipDTO.get(fileRelationshipDTO.size() - 1).getPathFile() : null)
                .address(user.getAddress())
                .isDeleted(user.getIsDeleted())
                .phoneNumber(user.getPhoneNumber())
                .profileLink(user.getProfileLink())
                .description(user.getDescription())
                .isEmailConfirmed(user.isEmailConfirmed)
                .build();
//        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> toDTOs(List<User> users) {
        if (users.isNullOrEmpty()) return new ArrayList<>();
        List<String> ids = users.stream().map(User::getId).collect(Collectors.toList());
        List<FileRelationshipDTO> fileRelationshipDTOS = fileRelationshipController.getFileRelationships(ids, EnumParentFileType.USER_AVATAR.name());
        Map<String, String> fileRelationshipDTOMap = fileRelationshipController.getUrlOfFile(fileRelationshipDTOS);
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(UserDTO.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .roles(user.getRoles())
                    .avatar(fileRelationshipDTOMap.get(user.getId()))
                    .address(user.getAddress())
                    .isDeleted(user.getIsDeleted())
                    .isEmailConfirmed(user.isEmailConfirmed)
                    .build());
        }
        return userDTOS;
    }
}
