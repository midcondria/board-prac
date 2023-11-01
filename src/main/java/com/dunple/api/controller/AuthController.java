package com.dunple.api.controller;

import com.dunple.api.config.AppConfig;
import com.dunple.api.config.data.Login;
import com.dunple.api.config.data.UserSession;
import com.dunple.api.request.LoginRequest;
import com.dunple.api.request.Signup;
import com.dunple.api.response.SessionResponse;
import com.dunple.api.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AppConfig appConfig;

    @PostMapping("/auth/signup")
    public void signup(@RequestBody Signup signup) {
        authService.signup(signup);
    }

    @PostMapping("/auth/login")
    public SessionResponse login(@RequestBody LoginRequest request) {
        String name = authService.signin(request);
        // key 만드는 메서드
//        SecretKey key = Jwts.SIG.HS256.key().build();
//        String strKey = Encoders.BASE64.encode(key.getEncoded());
        SecretKey secretKey = Keys.hmacShaKeyFor(appConfig.getJwtKey());
        // 토큰을 응답
        return new SessionResponse(Jwts.builder()
            .subject(name)
            .issuedAt(new Date())
            .signWith(secretKey)
            .compact());
    }

    @GetMapping("/auth")
    public String access(@Login UserSession session) {
        return session.getName() + " 님 안녕하세요";
    }
}
