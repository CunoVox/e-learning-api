package com.elearning.oath2;

import com.elearning.oath2.token.AccessTokenResponse;

public interface TokenResponseClient {

    AccessTokenResponse getAccessTokenByAuthorizationCode(String code);

    AccessTokenResponse getAccessTokenByRefreshToken(String refreshToken);

}
