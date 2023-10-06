package com.example.mockmvcintframework.testsupport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SpringTestBase {
    @Autowired
    protected TestCurrentTimeProvider currentTimeProvider;

    @AfterEach
    void tearDown() {
        currentTimeProvider.resetPretendTime();
    }
}
