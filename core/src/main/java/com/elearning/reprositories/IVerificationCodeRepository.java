package com.elearning.reprositories;

import com.elearning.entities.VerificationCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IVerificationCodeRepository  extends MongoRepository<VerificationCode, String> {
    Optional<VerificationCode> findById(@NotNull String id);
    List<VerificationCode> findAllByParentIdAndIsConfirmedIsFalse(String id);
    Optional<VerificationCode> findByParentIdAndCode(String id, String code);
}
