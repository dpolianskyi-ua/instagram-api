package com.example.mockmvcintframework.controller;

import com.example.mockmvcintframework.dto.InstagramCredentialsRequestDTO;
import com.github.instagram4j.instagram4j.IGClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.github.instagram4j.instagram4j.utils.IGChallengeUtils.resolveTwoFactor;

@RestController
@RequestMapping("/api/v1/instagram")
@Slf4j
public class InstagramController {

    private IGClient client;

    @PostMapping
    @SneakyThrows
    public void loginWith2FAToken(@RequestBody InstagramCredentialsRequestDTO dto) {
        client = IGClient.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .onTwoFactor((igClient, loginResponse) -> resolveTwoFactor(igClient, loginResponse, dto::getInputCode))
                .login();
    }

    @GetMapping("username")
    public ResponseEntity<String> getUsername() {
        return ResponseEntity.ok(client.getSelfProfile().getUsername());
    }
}
