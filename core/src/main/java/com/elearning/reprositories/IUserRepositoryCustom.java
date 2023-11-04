package com.elearning.reprositories;

import com.elearning.entities.User;
import com.elearning.models.searchs.ParameterSearchUser;
import com.elearning.models.wrapper.ListWrapper;

public interface IUserRepositoryCustom {
    ListWrapper<User> searchUser(ParameterSearchUser parameterSearchUser);
}
