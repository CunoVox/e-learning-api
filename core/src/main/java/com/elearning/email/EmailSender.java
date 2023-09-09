package com.elearning.email;

import com.elearning.entities.VerificationCode;

public interface EmailSender {
    void send(String to, String email);
    void sendMail(String to, VerificationCode code);
//    void sendUserEmailVerification(String to, VerificationCode code);
}
