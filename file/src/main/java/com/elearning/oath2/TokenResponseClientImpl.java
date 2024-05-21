package com.elearning.oath2;

import com.elearning.controller.BaseController;
import com.elearning.oath2.token.*;
import com.elearning.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@Component
@RequiredArgsConstructor
public class TokenResponseClientImpl implements TokenResponseClient {

    private final RestTemplate restTemplate;

    private final BaseController baseController;

    @Override
    public AccessTokenResponse getAccessTokenByAuthorizationCode(String code) {
        AuthorizationRequest request = AuthorizationCodeGrantRequest.builder()
                .clientId(baseController.getClientId())
                .clientSecret(baseController.getClientSecret())
                .redirectUri(baseController.getRedirectUri())
                .code(code)
                .build();

        RequestEntity<?> requestEntity = getRequestEntity(request);

        ResponseEntity<AccessTokenResponse> response = this.restTemplate.exchange(requestEntity, AccessTokenResponse.class);

        return response.getBody();
    }

    @Override
    public AccessTokenResponse getAccessTokenByRefreshToken(String refreshToken) {
        AuthorizationRequest request = RefreshTokenRequest.builder()
                .clientId(baseController.getClientId())
                .clientSecret(baseController.getClientSecret())
                .refreshToken(refreshToken)
                .build();

        RequestEntity<?> requestEntity = getRequestEntity(request);

        ResponseEntity<AccessTokenResponse> response = this.restTemplate.exchange(requestEntity, AccessTokenResponse.class);

        return response.getBody();
    }

    private RequestEntity<?> getRequestEntity(AuthorizationRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> body = request.getBody();

        return RequestEntity
                .post("https://oauth2.googleapis.com/token")
                .headers(headers)
                .body(body);
    }
}
