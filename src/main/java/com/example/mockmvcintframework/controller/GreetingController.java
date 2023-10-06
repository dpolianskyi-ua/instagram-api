package com.example.mockmvcintframework.controller;

import com.example.mockmvcintframework.dto.GreetingMessageRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

@RestController
@RequestMapping("/api/greeting")
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping
    public GreetingMessageRecord greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new GreetingMessageRecord(counter.incrementAndGet(), format(template, name));
    }
}
