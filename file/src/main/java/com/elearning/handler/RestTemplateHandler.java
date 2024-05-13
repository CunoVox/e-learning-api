package com.elearning.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.webjars.NotFoundException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;


@Component
public class RestTemplateHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series().equals(CLIENT_ERROR)
                || httpResponse.getStatusCode().series().equals(SERVER_ERROR));
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().series().equals(SERVER_ERROR)) {
            throw new ServiceException("Đã xảy ra lỗi");
        } else if (httpResponse.getStatusCode().series().equals(CLIENT_ERROR)) {
            if (httpResponse.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NotFoundException("Không tồn tại");
            }
        }
    }
}

