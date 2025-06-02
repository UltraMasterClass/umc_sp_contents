package com.umc.sp.contents.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static java.time.temporal.ChronoUnit.SECONDS;

@Configuration
public class ClockTestConfig {

    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.now().truncatedTo(SECONDS), ZoneId.of("UTC"));
    }
}
