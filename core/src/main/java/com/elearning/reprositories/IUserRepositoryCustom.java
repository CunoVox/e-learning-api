package com.elearning.reprositories;

import com.elearning.entities.User;
import com.elearning.models.searchs.ParameterSearchUser;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.utils.enumAttribute.EnumRole;

import java.util.List;

public interface IUserRepositoryCustom {
    ListWrapper<User> searchUser(ParameterSearchUser parameterSearchUser);
    void updateDeleted(String id, boolean deleted, String updateBy);

    void updateFullName(String id, String fullName, String updatedBy);
    void updatePhoneNumber(String id, String phoneNumber, String updatedBy);
    void updateAddress(String id, String address, String updatedBy);
    void updateUserRoles(String id, List<EnumRole> roles, String updatedBy);
}
