package com.elearning.controller;

import com.elearning.email.EmailSender;
import com.elearning.entities.User;
import com.elearning.entities.VerificationCode;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.VerificationCodeDTO;
import com.elearning.reprositories.IUserRepository;
import com.elearning.reprositories.IVerificationCodeRepository;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumVerificationCode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.elearning.utils.Constants.EMAIL_VERIFICATION_CODE_EXPIRE_TIME_MILLIS;

@Service
@AllArgsConstructor
public class VerificationCodeController {
    private final IVerificationCodeRepository verificationCodeRepository;
    private final UserController userController;
    private final EmailSender emailSender;
    @Autowired
    private IUserRepository userRepository;
    public void create(VerificationCode code) {
        if (code.getCode() == null) {
            code.setCode(code.getId());
        }
        verificationCodeRepository.save(code);
    }

    public VerificationCode build(String userId, String sendTo, String code, EnumVerificationCode type) {
        return VerificationCode.builder()
                .id(UUID.randomUUID().toString())
                .code(code)
                .type(type)
                .parentId(userId)
                .sendTo(sendTo)
                .isConfirmed(false)
                .isDeleted(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .confirmedAt(null)
                .expiredAt(new Date(System.currentTimeMillis() + EMAIL_VERIFICATION_CODE_EXPIRE_TIME_MILLIS))
                .build();
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

    public VerificationCodeDTO createEmailConfirmCode(String email) {
//        UserDTO dto = userController.findById(userId);
//        UserDTO dto = userController.findByEmail(email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            throw new ServiceException("Email đã tồn tại");
        }
        revokeAllUserEmailVerificationCodeByEmail(email);

        String digit = StringUtils.randomNumber(6);

        VerificationCode code = build(null, email, digit, EnumVerificationCode.EMAIL_CONFIRM);
        create(code);
        emailSender.sendMail(email, code);
        return toDTO(code);
    }

    public VerificationCodeDTO createResetPasswordCode(String email) {
        String digit = StringUtils.randomNumber(6);
        UserDTO dto = userController.findByEmail(email);
//        if (!dto.getIsEmailConfirmed()) {
//            throw new ServiceException("Email chưa được xác nhận");
//        }
        revokeAllUserResetPasswordCode(dto.getId());

        VerificationCode code = build(dto.getId(), dto.getEmail(), digit, EnumVerificationCode.RESET_PASSWORD_CONFIRM);
        create(code);
        emailSender.sendMail(dto.getEmail(), code);
        return toDTO(code);
    }

    public void revokeAllUserEmailVerificationCode(String userId) { // thu hồi tất cả code xác nhận của người dùng
        var validUserTokens = verificationCodeRepository.findAllByParentIdAndIsConfirmedIsFalse(userId);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            if (token.getType().equals(EnumVerificationCode.EMAIL_CONFIRM)) {
                token.setIsDeleted(true);
                token.setUpdatedAt(new Date());

            }
        });
        verificationCodeRepository.saveAll(validUserTokens);
    }
    public void revokeAllUserEmailVerificationCodeByEmail(String email) { // thu hồi tất cả code xác nhận của người dùng
        var validUserTokens = verificationCodeRepository.findAllBySendTo(email);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            if (token.getType().equals(EnumVerificationCode.EMAIL_CONFIRM)) {
                token.setIsDeleted(true);
                token.setUpdatedAt(new Date());

            }
        });
        verificationCodeRepository.saveAll(validUserTokens);
    }
    public void revokeAllUserResetPasswordCode(String userId) { // thu hồi tất cả code xác nhận của người dùng
        var validUserTokens = verificationCodeRepository.findAllByParentIdAndIsConfirmedIsFalse(userId);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            if (token.getType().equals(EnumVerificationCode.RESET_PASSWORD_CONFIRM)) {
                token.setIsDeleted(true);
                token.setUpdatedAt(new Date());

            }
        });
        verificationCodeRepository.saveAll(validUserTokens);
    }
    @Transactional
    public void emailConfirmCode(String email, String verifyCode) {
        Optional<VerificationCode> code = verificationCodeRepository.findBySendToAndCode(email, verifyCode);
        if (code.isEmpty()) {
            throw new ServiceException("Mã xác nhận Email không hợp lệ");
        } else {
            VerificationCode vCode = code.get();
            if (!vCode.getType().equals(EnumVerificationCode.EMAIL_CONFIRM)) {
                throw new ServiceException("Mã xác nhận Email không hợp lệ");
            } else {
                if (vCode.getIsDeleted()) {
                    throw new ServiceException("Mã xác nhận Email không hợp lệ");
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

//                userController.userEmailConfirm(vCode.getParentId());
                verificationCodeRepository.save(vCode);
            }
        }
    }
    public Boolean checkEmailConfirmCode(String email, String verifyCode) {
        Optional<VerificationCode> code = verificationCodeRepository.findBySendToAndCode(email, verifyCode);
        if (code.isEmpty()) {
            throw new ServiceException("Confirm code is invalid");
        } else {
            VerificationCode vCode = code.get();
            if (!vCode.getType().equals(EnumVerificationCode.EMAIL_CONFIRM)) {
                throw new ServiceException("Confirm code is invalid");
            } else {
                if (vCode.getIsDeleted()) {
                    throw new ServiceException("Confirm code is invalid");
                }
                if (vCode.getIsConfirmed() || vCode.getConfirmedAt() != null) {
                    throw new ServiceException("Confirm code is invalid");
                }
                if (vCode.getExpiredAt().before(new Date())) {
                    throw new ServiceException("Confirm code is expired");
                }
                return true;
            }
        }
    }
    public Boolean resetPasswordConfirmCode(String userId, String resetCode) {
        Optional<VerificationCode> code = verificationCodeRepository.findByParentIdAndCode(userId, resetCode);
        if (code.isEmpty()) {
            throw new ServiceException("Mã xác nhận không hợp lệ.");
        } else {
            VerificationCode vCode = code.get();
            if (!vCode.getType().equals(EnumVerificationCode.RESET_PASSWORD_CONFIRM)) {
                throw new ServiceException("Mã xác nhận không hợp lệ.");
            } else {
                if (vCode.getIsDeleted()) {
                    throw new ServiceException("Mã xác nhận không hợp lệ.");
                }
                if (vCode.getIsConfirmed() || vCode.getConfirmedAt() != null) {
                    throw new ServiceException("Mã xác nhận không hợp lệ.");
                }
                if (vCode.getExpiredAt().before(new Date())) {
                    throw new ServiceException("Mã xác nhận đã hết hạn.");
                }
                return true;
            }
        }
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

}
