package com.elearning.apis.user;

import com.elearning.controller.UserController;
import com.elearning.controller.VerificationCodeController;
import com.elearning.models.dtos.ResetPasswordDTO;
import com.elearning.models.dtos.UpdateUserDTO;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.UserEmailRequest;
import com.elearning.models.searchs.ParameterSearchUser;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.utils.enumAttribute.EnumRole;
import com.elearning.utils.enumAttribute.EnumUserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserAPI {
    @Autowired
    private UserController userController;
    private final VerificationCodeController verificationCodeController;

    @Operation(summary = "Cập nhật người dùng")
    @PatchMapping("/profile/update")
    public UserDTO update(@RequestBody UserDTO dto) {

        return userController.update(dto);
    }

    @Operation(summary = "Đăng kí làm giảng viên")
    @PostMapping("/lecturer/register")
    public UserDTO register(@RequestBody UserDTO dto) {
        return userController.userLecturerUpdate(dto);
    }

    @Operation(summary = "Khoá và mở khoá người dùng")
    @PostMapping("/lock/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public void lockAndUnLockUser(@PathVariable(value = "id") String id,
                                  @RequestParam(value = "lock") boolean lock) {
        userController.lockAndUnLockUser(id, lock);
    }

    @Operation(summary = "Cập nhật vai trò người dùng")
    @PutMapping("/update-roles/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public void updateRoles(@PathVariable("id") String id,
                            @RequestParam(value = "roles", required = false) List<EnumRole> roles) {
        userController.updateRoles(id, roles);
    }

    @Operation(summary = "Cập nhật tên người dùng")
    @PutMapping("/update-full-name/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public void updateFullName(@PathVariable("id") String id,
                               @RequestParam(value = "full_name") String fullName) {
        userController.updateFullName(id, fullName);
    }

    @Operation(summary = "Cập nhật số điện thoại người dùng")
    @PutMapping("/update-phone-number/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public void updatePhoneNumber(@PathVariable("id") String id,
                                  @RequestParam(value = "phone_number") String phoneNumber) {
        userController.updatePhoneNumber(id, phoneNumber);
    }

    @Operation(summary = "Cập nhật địa chỉ người dùng")
    @PutMapping("/update-address/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public void updateAddress(@PathVariable("id") String id,
                              @RequestParam(value = "address") String address) {
        userController.updateAddress(id, address);
    }

    @Operation(summary = "Xin gửi mail reset password")
    @PostMapping(value = "/password/reset")
    public ResponseEntity<?> sendEmailResetPassword(@RequestBody @Valid UserEmailRequest request) {
        return ResponseEntity.ok().body(verificationCodeController.createResetPasswordCode(request.getEmail()));
    }

    @Operation(summary = "Reset mật khẩu")
    @PatchMapping(value = "/password/reset")
    public void resetPassword(@RequestBody ResetPasswordDTO dto) {
        userController.userResetPassword(dto.getEmail(), dto);
    }

    @Operation(summary = "Chi tiết người dùng")
    @GetMapping(value = "/detail/{id}")
    public UserDTO userDetail(@PathVariable(value = "id") String id) {
        return userController.getUserDetail(id);
    }

    @Operation(summary = "Danh sách người dùng")
    @GetMapping(value = "/")
    public ListWrapper<UserDTO> getUser(@RequestParam(value = "status") EnumUserStatus status,
                                        @RequestParam(value = "from_date", required = false) Long fromDate,
                                        @RequestParam(value = "to_date", required = false) Long toDate,
                                        @RequestParam(value = "key_word", required = false) String multiValue,
                                        @RequestParam(value = "user_ids", required = false) List<String> userIds,
                                        @RequestParam(value = "roles", required = false) List<EnumRole> roles,
                                        @RequestParam(value = "current_page", required = false) @Min(value = 1, message = "currentPage phải lớn hơn 0") @Parameter(description = "Default: 1") Integer currentPage,
                                        @RequestParam(value = "max_result", required = false) @Min(value = 1, message = "maxResult phải lớn hơn 0") @Max(value = 100, message = "maxResult phải bé hơn hoặc bằng 100") @Parameter(description = "Default: 20; Size range: 1-100") Integer maxResult
    ) {
        ParameterSearchUser parameterSearchUser = new ParameterSearchUser();
        parameterSearchUser.setStatus(status);
        if (currentPage == null || currentPage == 0) {
            currentPage = 1;
        }
        if (maxResult == null || maxResult == 0) {
            maxResult = 20;
        }
        Long startIndex = ((long) (currentPage - 1) * maxResult);
        if (fromDate != null) {
            parameterSearchUser.setFromDate(new Date(fromDate));
        }
        if (toDate != null) {
            parameterSearchUser.setToDate(new Date(toDate));
        }
        parameterSearchUser.setMultiValue(multiValue);
        parameterSearchUser.setUserIds(userIds);
        parameterSearchUser.setRoles(roles);
        parameterSearchUser.setStartIndex(startIndex);
        parameterSearchUser.setMaxResult(maxResult);
        return userController.searchUser(parameterSearchUser);
    }
}
