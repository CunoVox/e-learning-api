package com.elearning.reprositories;

import com.elearning.entities.RefreshToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.sql.Ref;
import java.util.List;
import java.util.Optional;


@Repository
public interface IRefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findById(@NotNull String id);
    List<RefreshToken> findByUserId(String userId);
}
