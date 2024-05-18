package com.elearning.controller;

import com.elearning.entities.ConfigProperty;
import com.elearning.handler.ServiceException;
import com.elearning.models.wrapper.ObjectResponseWrapper;
import com.elearning.oath2.TokenResponseClient;
import com.elearning.oath2.token.AccessTokenResponse;
import com.elearning.reprositories.IConfigPropertyRepository;
import com.elearning.utils.Constants;
import com.elearning.utils.DateUtils;
import com.elearning.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private String accessToken;

    private final TokenResponseClient tokenResponseClient;

    //============AUTH_CONTROLLER================
    public String getAuthUrl() {
        log.info("----Get Auth URL----");
        String scope = "https://www.googleapis.com/auth/youtube";
        return UriComponentsBuilder.fromHttpUrl(getAuthHost())
                .queryParam("response_type", "code")
                .queryParam("client_id", getClientId())
                .queryParam("redirect_uri", Constants.REDIRECT_URL)
                .queryParam("scope", scope)
                .queryParam("o2v", "1")
                .queryParam("ddm", "0")
                .queryParam("flowName", "GeneralOAuthFlow")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .toUriString();
    }

    @SneakyThrows
    public ObjectResponseWrapper createToken(String code) {
        log.info("----Create Access Token----");
        AccessTokenResponse accessTokenResponse = tokenResponseClient.getAccessTokenByAuthorizationCode(code);
        if (accessTokenResponse == null) {
            throw new ServiceException("GET_ACCESS_TOKEN_FAILED");
        }

//        saveToken(config, accessTokenResponse, accountInfo);

        accessToken = accessTokenResponse.getAccessToken();
        saveToken(accessTokenResponse.getAccessToken(), accessTokenResponse.getRefreshToken());
        return ObjectResponseWrapper.builder()
                .status(1)
                .message("Get access token successfully")
                .data(accessTokenResponse)
                .build();
    }

    public void refreshToken() {
        log.info("*** Begin scheduler refresh token at {} ***", DateUtils.convertDateToString(new Date(), "dd/MM/yyyy HH:mm:ss"));
        String refreshToken = getRefreshToken();
        if (!StringUtils.isBlankOrNull(refreshToken)) {
            try {
                AccessTokenResponse accessTokenResponse = tokenResponseClient.getAccessTokenByRefreshToken(refreshToken);
                if (accessTokenResponse == null) {
                    throw new ServiceException("GET_ACCESS_TOKEN_FAILED");
                }
                accessToken = accessTokenResponse.getAccessToken();
                saveToken(accessToken, refreshToken);
                log.info("Refresh token successfully");
            } catch (Exception e) {
                if (e instanceof HttpClientErrorException) {
                    HttpClientErrorException restTemplateEx = (HttpClientErrorException) e;
                    String message = restTemplateEx.getResponseBodyAsString();
                    log.error(message);
                } else {
                    log.error(e.getMessage());
                }
            }
        }

        log.info("*** End scheduler refresh token at {} ***", DateUtils.convertDateToString(new Date(), "dd/MM/yyyy HH:mm:ss"));
        log.info("==========================================================================");
    }


    public String getAccessTokenFromDB() {
        accessToken = getToken();
        if (StringUtils.isBlankOrNull(accessToken)) {
            throw new ServiceException("Chưa cấu hình upload video");
        }
        return accessToken;
    }
}
