package com.example.mockmvcintframework.utils;

import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import java.util.TimeZone;

public class CurrentTimeProvider implements DateTimeProvider {
    public static final ZoneId EST = TimeZone.getTimeZone("EST").toZoneId();

    public CurrentTimeProvider() {
        TimeZone.setDefault(TimeZone.getTimeZone(EST));
    }

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    public LocalDate getLocalDate() {
        return LocalDate.now();
    }

    public LocalDateTime getDefaultHistoryStartDate() {
        return getLocalDateTime().minusDays(90);
    }

    public LocalDateTime getDefaultHistoryEndDate() {
        return getLocalDateTime();
    }

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(ZonedDateTime.of(getLocalDateTime(), ZoneId.systemDefault()));
    }
}
