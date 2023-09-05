package com.elearning.schedule;

import com.elearning.controller.RefreshTokenController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.elearning.utils.Constants.REFRESH_TOKEN_EXPIRE_TIME_MILLIS;

@Component
@Slf4j
public class DeteleTokenSchedule {
    @Autowired
    private RefreshTokenController refreshTokenController;
    @Scheduled(fixedRate = REFRESH_TOKEN_EXPIRE_TIME_MILLIS)
    public void deleteExpiredToken() {
        log.error(String.valueOf(new Date().toString()));
        refreshTokenController.deleteExpiredToken();
    }
}
