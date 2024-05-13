package com.elearning.controller;

import com.elearning.entities.ConfigProperty;
import com.elearning.reprositories.IConfigPropertyRepository;
import com.elearning.utils.Extensions;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ExtensionMethod(Extensions.class)
public class BaseController {
    @Autowired
    private IConfigPropertyRepository configPropertyRepository;


    public String getClientId() {
        return configPropertyRepository.findByName("client_id").getValue();
    }
    public String getClientSecret() {
        return configPropertyRepository.findByName("client_secret").getValue();
    }

    public String getRedirectUri() {
        return configPropertyRepository.findByName("redirect_uri").getValue();
    }

    public String getRefreshToken() {
        return configPropertyRepository.findByName("refresh_token").getValue();
    }

    public String getToken() {
        return configPropertyRepository.findByName("access_token").getValue();
    }

    public String getAuthHost() {
        return configPropertyRepository.findByName("auth_host").getValue();
    }

    public void saveToken(String accessToken, String refreshToken) {
        ConfigProperty accessTokenConfig = configPropertyRepository.findByName("access_token");
        ConfigProperty refreshTokenConfig = configPropertyRepository.findByName("refresh_token");
        if (accessTokenConfig == null) {
            accessTokenConfig = new ConfigProperty();
            accessTokenConfig.setName("access_token");
        }
        if (refreshTokenConfig == null) {
            refreshTokenConfig = new ConfigProperty();
            refreshTokenConfig.setName("refresh_token");
        }
        accessTokenConfig.setValue(accessToken);
        refreshTokenConfig.setValue(refreshToken);
        configPropertyRepository.save(accessTokenConfig);
        configPropertyRepository.save(refreshTokenConfig);
    }
}
