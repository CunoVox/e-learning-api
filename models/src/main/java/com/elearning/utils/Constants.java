package com.elearning.utils;

public class Constants {

    public static final String SERVICE_URL = "https://e-learning.up.railway.app/e-learning";
    public static final String WEB_URL = "https://e-learning-website.up.railway.app";

    public static final int ACCESS_TOKEN_EXPIRE_TIME_MILLIS = 100 * 60 * 1000; // ( 10' //100'
    public static final int REFRESH_TOKEN_EXPIRE_TIME_MILLIS = 24 * 60 * 60 * 7 * 1000; // 7 days
    public static final int EMAIL_VERIFICATION_CODE_EXPIRE_TIME_MILLIS = 5 * 60 * 1000; // 5'

    public static final String REFRESH_TOKEN_COOKIE_NAME = "X-REFRESH-TOKEN";
    public static final String SECRET_KEY = "KJHUIknjkNIOASpASouFaoiDFsfhnIOUUIvaJKJkasjAASD";

    public static final String FOLDER_TO_UPLOAD ="1vZDxFhds8Q9Tq5KdUIf1SH4YDHSHuTc6";
    public static final String SERVICE_ACCOUNT_ID = "e-learning@e-learning-399406.iam.gserviceaccount.com";

    public static final String BASE_IMAGE_URL = "https://lh3.googleusercontent.com/d/";
    public static final String BASE_VIDEO_URL = "https://drive.google.com/file/d/";


    //VNPAY
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_RETURN_URL = WEB_URL + "/vnpay-return";
    public static final String VNP_TMN_CODE = "FV2FPW0B";
    public static final String VNP_SECRET_KEY = "TSYFMBEBBUSUVSBPNQCWEFQDIMEBKFBD";
    public static final String VNP_API_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND= "pay";

}
