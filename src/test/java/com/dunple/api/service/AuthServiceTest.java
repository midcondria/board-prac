package com.dunple.api.service;

import com.dunple.api.crypto.PasswordEncoder;
import com.dunple.api.domain.User;
import com.dunple.api.exception.AlreadyExistEmailException;
import com.dunple.api.exception.InvalidSigninInformationException;
import com.dunple.api.repository.UserRepository;
import com.dunple.api.request.LoginRequest;
import com.dunple.api.request.Signup;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("default")
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @DisplayName("회원가입 성공")
    @Test
    void signup() {
        // given
        Signup signup = Signup.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password("1234")
            .build();

        // when
        authService.signup(signup);
        User user = userRepository.findByEmail("hyukkind@naver.com")
            .orElseThrow(() -> new InvalidSigninInformationException());

        // then
        assertEquals(1, userRepository.count());
        assertEquals("hyukkind@naver.com", user.getEmail());
        assertEquals("midcon", user.getName());
        assertTrue(encoder.matches("1234", user.getPassword()));
    }

    @DisplayName("중복된 이메일로 회원가입 시 예외가 발생")
    @Test
    void signup2() {
        // given
        User user = User.builder()
            .email("hyukkind@naver.com")
            .name("mika")
            .password("1234")
            .build();
        userRepository.save(user);

        Signup signup = Signup.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password("1234")
            .build();

        // expected
        assertThrows(AlreadyExistEmailException.class,
            () -> authService.signup(signup));
    }


    @DisplayName("로그인 요청 시 DB 정보와 일치하면 accessToken 을 발급한다.")
    @Test
    void signin() throws JsonProcessingException {
        // given
        Signup signup = Signup.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password("1234")
            .build();
        authService.signup(signup);

        LoginRequest request = LoginRequest.builder()
            .email("hyukkind@naver.com")
            .password("1234")
            .build();

        // when
        String name = authService.signin(request);
        User user = userRepository.findByEmail("hyukkind@naver.com")
            .orElseThrow(() -> new InvalidSigninInformationException());

        // then
        assertEquals("midcon", name);
    }

    @DisplayName("로그인 요청 시 DB 정보와 일치하지 않으면 예외가 발생한다.")
    @Test
    void signin2() throws JsonProcessingException {
        // given
        LoginRequest request = LoginRequest.builder()
            .email("hyukkind@naver.com")
            .password("1111")
            .build();

        // expected
        assertThrows(
            InvalidSigninInformationException.class,
            () -> authService.signin(request)
        );
    }
}