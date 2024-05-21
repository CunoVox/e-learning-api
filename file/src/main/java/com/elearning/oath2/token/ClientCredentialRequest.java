package com.elearning.oath2.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;


@Data
@Builder
public class ClientCredentialRequest implements AuthorizationRequest {

    @JsonProperty("grant_type")
    private final String grantType = "client_credentials";

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @Override
    public LinkedMultiValueMap<String, String> getBody() {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.put("grant_type", Collections.singletonList(grantType));
        body.put("client_id", Collections.singletonList(clientId));
        body.put("client_secret", Collections.singletonList(clientSecret));
        return body;
    }

    @Override
    public String getAuthorizationHeader() {
        String credentials = clientId + ":" + clientSecret;
        return "Basic " + Base64Utils.encodeToString(credentials.getBytes());
    }
}
