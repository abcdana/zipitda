package com.danahub.zipitda.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNumberGenerator {

    private final StringRedisTemplate redisTemplate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String generate(String prefix) {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String redisKey = "order_seq:" + prefix + ":" + dateStr;

        Long sequence = redisTemplate.opsForValue().increment(redisKey);
        if (sequence != null && sequence == 1L) {
            redisTemplate.expire(redisKey, Duration.ofDays(1));
        }

        return String.format("%s%s-%06d", prefix, dateStr, sequence);
    }
}