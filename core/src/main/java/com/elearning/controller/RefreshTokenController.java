package com.elearning.controller;

import com.elearning.entities.RefreshToken;
import com.elearning.reprositories.IRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class RefreshTokenController {
    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtController jwtController;
    public String create(String token, String userId) {
        RefreshToken rfToken = RefreshToken
                .builder()
                .id(token)
                .userId(userId)
                .createdAt(new Date(new java.util.Date().getTime()))
                .updatedAt(new Date(new java.util.Date().getTime()))
                .isDeleted(false)
                .expiredAt(jwtController.extractExpiration(token))
                .build();
//        User user = dtoToUser(dto);
//        user.roles.add(EnumRole.User);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user = userRepository.save(user);
        save(rfToken);
        return rfToken.getId();
    }
    public void save(RefreshToken token){
        refreshTokenRepository.save(token);
    }
    public Optional<RefreshToken> findById(String token){
        return refreshTokenRepository.findById(token);
    }
}
