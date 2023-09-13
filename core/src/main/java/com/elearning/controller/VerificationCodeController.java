package com.elearning.controller;

import com.elearning.email.EmailSender;
import com.elearning.entities.VerificationCode;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.VerificationCodeDTO;
import com.elearning.reprositories.IVerificationCodeRepository;
import com.elearning.utils.enumAttribute.EnumVerificationCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.elearning.utils.Constants.EMAIL_VERIFICATION_CODE_EXPIRE_TIME_MILLIS;

@Service
@AllArgsConstructor
public class VerificationCodeController {
    private final IVerificationCodeRepository verificationCodeRepository;
    private final UserController userController;
    private final EmailSender emailSender;

    public void create(VerificationCode code) {
        if (code.getCode() == null) {
            code.setCode(code.getId());
        }
        verificationCodeRepository.save(code);
    }

    public VerificationCode build(String userId, String code, EnumVerificationCode type) {
        return VerificationCode.builder()
                .id(UUID.randomUUID().toString())
                .code(code)
                .type(type)
                .parentId(userId)
                .isConfirmed(false)
                .isDeleted(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .confirmedAt(null)
                .expiredAt(new Date(System.currentTimeMillis() + EMAIL_VERIFICATION_CODE_EXPIRE_TIME_MILLIS))
                .build();
    }

    @Transactional
    public void emailConfirmCode(String userId, String verifyCode) {
        Optional<VerificationCode> code = verificationCodeRepository.findByParentIdAndCode(userId, verifyCode);
        if (code.isEmpty()) {
            throw new ServiceException("Mã xác nhận Email không hợp lệ 1.");
        } else {
            VerificationCode vCode = code.get();
            if (!vCode.getType().equals(EnumVerificationCode.EMAIL_CONFIRM)) {
                throw new ServiceException("Mã xác nhận Email không hợp lệ 2.");
            } else {
                if (vCode.getIsDeleted()) {
                    throw new ServiceException("Mã xác nhận Email không hợp lệ 3.");
                }
                if (vCode.getIsConfirmed() || vCode.getConfirmedAt() != null) {
                    throw new ServiceException("Email đã được xác nhận.");
                }
                if (vCode.getExpiredAt().before(new Date())) {
                    throw new ServiceException("Mã xác nhận Email đã hết hạn.");
                }

                vCode.setIsConfirmed(true);
                vCode.setIsDeleted(true);
                vCode.setConfirmedAt(new Date());
                vCode.setUpdatedAt(new Date());

                userController.userEmailConfirm(vCode.getParentId());
                verificationCodeRepository.save(vCode);
            }
        }
    }

    public void resetPasswordConfirmCode(String userId, String resetCode) {
        Optional<VerificationCode> code = verificationCodeRepository.findByParentIdAndCode(userId, resetCode);
        if (code.isEmpty()) {
            throw new ServiceException("Mã xác nhận không hợp lệ 1.");
        } else {
            VerificationCode vCode = code.get();
            if (!vCode.getType().equals(EnumVerificationCode.RESET_PASSWORD_CONFIRM)) {
                throw new ServiceException("Mã xác nhận không hợp lệ 2.");
            } else {
                if (vCode.getIsDeleted()) {
                    throw new ServiceException("Mã xác nhận không hợp lệ 3.");
                }
                if (vCode.getIsConfirmed() || vCode.getConfirmedAt() != null) {
                    throw new ServiceException("Mã xác nhận không hợp lệ 4.");
                }
                if (vCode.getExpiredAt().before(new Date())) {
                    throw new ServiceException("Mã xác nhận đã hết hạn.");
                }

//                vCode.setIsConfirmed(true);
//                vCode.setIsDeleted(true);
//                vCode.setConfirmedAt(new Date());
//                vCode.setUpdatedAt(new Date());
//
//                verificationCodeRepository.save(vCode);
            }
        }
    }

    public void confirmCode(String userId, String resetCode) {
        Optional<VerificationCode> code = verificationCodeRepository.findByParentIdAndCode(userId, resetCode);
        if (code.isPresent()) {
            VerificationCode vCode = code.get();

            vCode.setIsConfirmed(true);
            vCode.setIsDeleted(true);
            vCode.setConfirmedAt(new Date());
            vCode.setUpdatedAt(new Date());

            verificationCodeRepository.save(vCode);
        }
    }

    public VerificationCodeDTO createEmailConfirmCode(String userId) {
        UserDTO dto = userController.findById(userId);
        if (dto.getIsEmailConfirmed()) {
            throw new ServiceException("Email đã được xác nhận");
        }
        revokeAllUserEmailVerificationCode(userId);

        VerificationCode code = build(userId, null, EnumVerificationCode.EMAIL_CONFIRM);
        create(code);
        emailSender.sendMail(dto.getEmail(), code);
        return toDTO(code);
    }

    public VerificationCodeDTO createResetPasswordCode(String email) {
        String digit = getRandomNumberString();
        UserDTO dto = userController.findByEmail(email);
//        if (!dto.getIsEmailConfirmed()) {
//            throw new ServiceException("Email chưa được xác nhận");
//        }
        revokeAllUserResetPasswordCode(dto.getId());

        VerificationCode code = build(dto.getId(), digit, EnumVerificationCode.RESET_PASSWORD_CONFIRM);
        create(code);
        emailSender.sendMail(dto.getEmail(), code);
        return toDTO(code);
    }

    public void revokeAllUserEmailVerificationCode(String userId) { // thu hồi tất cả code xác nhận của người dùng
        var validUserTokens = verificationCodeRepository.findAllByParentIdAndIsConfirmedIsFalse(userId);
        if (validUserTokens.isEmpty())
            return;
        for (VerificationCode token : validUserTokens) {
            if (token.getType().equals(EnumVerificationCode.EMAIL_CONFIRM)) {
                token.setIsDeleted(true);
                token.setUpdatedAt(new Date());

            }
        }
        verificationCodeRepository.saveAll(validUserTokens);
    }

    public void revokeAllUserResetPasswordCode(String userId) { // thu hồi tất cả code xác nhận của người dùng
        var validUserTokens = verificationCodeRepository.findAllByParentIdAndIsConfirmedIsFalse(userId);
        if (validUserTokens.isEmpty())
            return;
        for (VerificationCode token : validUserTokens) {
            if (token.getType().equals(EnumVerificationCode.RESET_PASSWORD_CONFIRM)) {
                token.setIsDeleted(true);
                token.setUpdatedAt(new Date());

            }
        }
        verificationCodeRepository.saveAll(validUserTokens);
    }

    public VerificationCodeDTO toDTO(VerificationCode code) {
        return VerificationCodeDTO.builder()
                .id(code.getId())
                .code(code.getCode())
                .parentId(code.getParentId())
                .type(code.getType())
                .confirmedAt(code.getConfirmedAt())
                .expiredAt(code.getExpiredAt())
                .build();
    }

    private String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
