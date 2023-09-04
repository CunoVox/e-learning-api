package com.elearning.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ServiceException extends ResponseStatusException {

    @Getter
    private Object data;

    public ServiceException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ServiceException(String message, Object data) {
        super(HttpStatus.NOT_FOUND, message);
        this.data = data;
    }

    @Override
    public String getMessage() {
        return super.getMessage().replaceAll("404 NOT_FOUND", "");
    }
}
