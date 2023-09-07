package com.elearning.controller;

import com.elearning.entities.User;
import com.elearning.entities.VerificationCode;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.VerificationCodeDTO;
import com.elearning.reprositories.IVerificationCodeRepository;
import com.elearning.utils.enumAttribute.EnumVerificationCode;
import lombok.AllArgsConstructor;
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
    public void create(VerificationCode code){
        if(code.getCode() == null){
            code.setCode(code.getId());
        }
        verificationCodeRepository.save(code);
    }
    public VerificationCode build(String userId, String code, EnumVerificationCode type){
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
    public void EmailConfirmCode(String verifyCode){
        Optional<VerificationCode> code = verificationCodeRepository.findById(verifyCode);
        if(code.isEmpty()){
            throw new ServiceException("Mã xác nhận Email không hợp lệ 1.");
        }else{
            VerificationCode vCode = code.get();
            if (!vCode.getType().equals(EnumVerificationCode.EMAIL_CONFIRM)){
                throw new ServiceException("Mã xác nhận Email không hợp lệ 2.");
            }else{
                if(vCode.getIsDeleted()){
                    throw new ServiceException("Mã xác nhận Email không hợp lệ 3.");
                }
                if (vCode.getIsConfirmed() || vCode.getConfirmedAt() != null){
                    throw new ServiceException("Email đã được xác nhận.");
                }
                if(vCode.getExpiredAt().before(new Date())){
                    throw new ServiceException("Mã xác nhận Email đã hết hạn.");
                }

                vCode.setIsConfirmed(true);
                vCode.setIsDeleted(true);
                vCode.setConfirmedAt(new Date());
                vCode.setUpdatedAt(new Date());

                userController.userEmailConfirmed(vCode.getParentId());
                verificationCodeRepository.save(vCode);
            }
        }
    }
    public VerificationCodeDTO reCreateEmailCode(String userId){
        UserDTO dto = userController.findById(userId);
        if(dto.getIsEmailConfirmed()){
            throw new ServiceException("Email đã được xác nhận");
        }
        revokeAllUserVerificationCode(userId);

        VerificationCode code = build(userId,null, EnumVerificationCode.EMAIL_CONFIRM);
        create(code);
        return toDTO(code);
    }
    public void reCreateResetPasswordCode(){}
    public void revokeAllUserVerificationCode(String userId) { // thu hồi tất cả code xác nhận của người dùng
        var validUserTokens = verificationCodeRepository.findAllByParentIdAndIsConfirmedIsFalse(userId);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setIsDeleted(true);
            token.setUpdatedAt(new Date());
        });
        verificationCodeRepository.saveAll(validUserTokens);
    }

    public VerificationCodeDTO toDTO(VerificationCode code){
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
