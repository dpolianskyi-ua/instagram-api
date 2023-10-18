package com.example.mockmvcintframework.controller;

import com.example.mockmvcintframework.dto.InstagramCredentialsRequestDTO;
import com.example.mockmvcintframework.service.InstagramService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/instagram/login")
@RequiredArgsConstructor
public class InstagramLoginController {

    private final InstagramService instagramService;

    @PostMapping()
    @SneakyThrows
    public void loginWithPassword(@RequestBody InstagramCredentialsRequestDTO dto) {
        instagramService.initializeClient(dto);
    }

    @PostMapping("2fa")
    @SneakyThrows
    public void loginWith2FAToken(@RequestBody InstagramCredentialsRequestDTO dto) {
        instagramService.initializeClient(dto);
    }
}
