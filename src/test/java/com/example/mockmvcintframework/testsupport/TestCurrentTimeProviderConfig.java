package com.example.mockmvcintframework.testsupport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestCurrentTimeProviderConfig {
    @Bean
    @Primary
    public TestCurrentTimeProvider testCurrentTimeProvider() {
        return new TestCurrentTimeProvider();
    }
}
