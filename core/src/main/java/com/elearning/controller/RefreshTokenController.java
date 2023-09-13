package com.elearning.controller;

import com.elearning.entities.RefreshToken;
import com.elearning.handler.ServiceException;
import com.elearning.reprositories.IRefreshTokenRepository;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
                .createdAt(new Date())
                .updatedAt(new Date())
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

    public void save(RefreshToken token) {
        refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findById(String token) {
        return refreshTokenRepository.findById(token);
    }

    private void deleteRefreshToken(String token) {
        List<RefreshToken> refreshTokenList = refreshTokenRepository.findAll();
        List<RefreshToken> toDeleteList;
//        for (RefreshToken refreshToken : refreshTokenList) {
//            if (refreshToken.getId().equals(token) || (refreshToken.getCreatedFrom() != null
//                    && refreshToken.getCreatedFrom().equals(token))) {
//                toDeleteList.add(refreshToken);
//            }
//        }
        toDeleteList = refreshTokenList.stream().filter(refreshToken->refreshToken.getId().equals(token) || (refreshToken.getCreatedFrom() != null
                && refreshToken.getCreatedFrom().equals(token))).collect(Collectors.toList());
        refreshTokenRepository.deleteAll(toDeleteList);
    }

    public void deleteRefreshTokenBranch(String token) {
        var refreshToken = refreshTokenRepository.findById(token);

        if (!refreshToken.isPresent()) {
            throw new ServiceException("Lỗi");
        } else {
            if (refreshToken.get().getCreatedFrom() == null) {
                deleteRefreshToken(refreshToken.get().getId());
            } else {
                deleteRefreshToken(refreshToken.get().getCreatedFrom());
            }
        }
    }
    public void deleteExpiredToken(){
        var refreshTokens = refreshTokenRepository.findAll();
        List<RefreshToken> toDeleteList = refreshTokens.stream().filter(refreshToken ->
            refreshToken.getExpiredAt().before(new Date())
        ).collect(Collectors.toList());
        refreshTokenRepository.deleteAll(toDeleteList);
    }
}
