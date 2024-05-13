package com.elearning.schedule;

import com.elearning.controller.AuthController;
import com.elearning.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenSchedule {
    @Autowired
    private AuthController authController;
    @Scheduled(cron = "0 */30 * * * *") // scheduler run every 30 minutes
    public void refreshTokenStoreScheduler() {
        authController.refreshToken();
    }
}
