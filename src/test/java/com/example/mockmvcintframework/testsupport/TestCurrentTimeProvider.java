package com.example.mockmvcintframework.testsupport;

import com.example.mockmvcintframework.utils.CurrentTimeProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TestCurrentTimeProvider extends CurrentTimeProvider {
    private static final LocalDateTime DEFAULT_PRETEND_DATE_TIME = LocalDateTime.of(2019, 1, 1, 12, 0);
    private LocalDateTime pretendTime = DEFAULT_PRETEND_DATE_TIME;

    @Override
    public LocalDateTime getLocalDateTime() {
        return pretendTime;
    }

    @Override
    public LocalDate getLocalDate() {
        return pretendTime.toLocalDate();
    }

    public void setPretendTime(LocalDateTime pretendTime) {
        this.pretendTime = pretendTime;
    }

    public void setPretendTime(Long timeInMillis) {
        this.pretendTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneId.systemDefault());
    }

    public void resetPretendTime() {
        this.pretendTime = DEFAULT_PRETEND_DATE_TIME;
    }
}
