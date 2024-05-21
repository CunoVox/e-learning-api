package com.elearning.oath2.token;

import org.springframework.util.LinkedMultiValueMap;


public interface AuthorizationRequest {

    LinkedMultiValueMap<String, String> getBody();

    String getAuthorizationHeader();
    
}
