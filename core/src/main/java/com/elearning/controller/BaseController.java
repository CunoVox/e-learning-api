package com.elearning.controller;

import com.elearning.security.SecurityUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
    public String getUserIdFromContext(){
        var context = SecurityContextHolder.getContext().getAuthentication();
        SecurityUserDetail userDetail = (SecurityUserDetail) context.getPrincipal();
        if (userDetail != null){
            return userDetail.getId();
        }
        return null;
    }
    public SecurityUserDetail getUserDetailFromContext(){
        var context = SecurityContextHolder.getContext().getAuthentication();
        return (SecurityUserDetail) context.getPrincipal();
    }
}
