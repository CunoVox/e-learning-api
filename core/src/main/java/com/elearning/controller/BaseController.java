package com.elearning.controller;

import com.elearning.handler.ServiceException;
import com.elearning.security.SecurityUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public abstract class BaseController {
    public String getUserIdFromContext() {
        try {
            var context = SecurityContextHolder.getContext().getAuthentication();
            log.info(context.getAuthorities().toString());
            SecurityUserDetail userDetail = (SecurityUserDetail) context.getPrincipal();
            if (userDetail != null) {
                return userDetail.getId();
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException("Lỗi khi lấy thông tin người dùng.");
        }
    }

    public SecurityUserDetail getUserDetailFromContext() {
        var context = SecurityContextHolder.getContext().getAuthentication();
        return (SecurityUserDetail) context.getPrincipal();
    }
}
