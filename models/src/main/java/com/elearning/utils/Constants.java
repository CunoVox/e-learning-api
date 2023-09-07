package com.elearning.utils;

public class Constants {

    public static final String SERVICE_URL = "http://localhost:8080/e-learning";

    public static final int ACCESS_TOKEN_EXPIRE_TIME_MILLIS = 10 * 60 * 1000; // ( 10'
    public static final int REFRESH_TOKEN_EXPIRE_TIME_MILLIS = 24 * 60 * 60 * 7 * 1000; // 7 days
    public static final int EMAIL_VERIFICATION_CODE_EXPIRE_TIME_MILLIS = 5 * 60 * 1000; // 5'

    public static final String REFRESH_TOKEN_COOKIE_NAME = "X-REFRESH-TOKEN";
    public static final String SECRET_KEY = "KJHUIknjkNIOASpASouFaoiDFsfhnIOUUIvaJKJkasjAASD";

}
