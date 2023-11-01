package com.dunple.api.controller;

import com.dunple.api.crypto.PasswordEncoder;
import com.dunple.api.domain.User;
import com.dunple.api.repository.SessionRepository;
import com.dunple.api.repository.UserRepository;
import com.dunple.api.request.LoginRequest;
import com.dunple.api.request.Signup;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    @DisplayName("회원가입 성공")
    @Test
    void signup() throws Exception {
        // given
        Signup signup = Signup.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password("1234")
            .build();

        String json = objectMapper.writeValueAsString(signup);

        // expected
        mockMvc.perform(
            post("/auth/signup")
                .content(json)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("중복된 이메일로 회원가입 시 회원가입 실패")
    @Test
    void signup2() throws Exception {
        // given
        User user = User.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password(encoder.encrypt("1234"))
            .build();
        userRepository.save(user);

        Signup signup = Signup.builder()
            .email("hyukkind@naver.com")
            .name("mika")
            .password("1234")
            .build();

        String json = objectMapper.writeValueAsString(signup);

        // expected
        mockMvc.perform(
                post("/auth/signup")
                    .content(json)
                    .contentType(APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
    }

    @DisplayName("로그인 성공")
    @Test
    void login() throws Exception {
        // given
        User user = User.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password(encoder.encrypt("1234"))
            .build();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
            .email("hyukkind@naver.com")
            .password("1234")
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(
            post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("로그인 실패")
    @Test
    void login2() throws Exception {
        // given
        User user = User.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password(encoder.encrypt("1234"))
            .build();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
            .email("hyukkind@naver.com")
            .password("1111")
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(
                post("/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(json)
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("검증된 세션 값으로 권한이 필요한 페이지에 요청시 접속에 성공한다.")
    @Test
    void access() throws Exception {
        // given
        User user = User.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password(encoder.encrypt("1234"))
            .build();
        userRepository.save(user);

        // expected
        mockMvc.perform(
                get("/auth")
                    .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtaWRjb24ifQ.u_88_mWPpVXBTF1l4LcbYnhOJMS2Iksz25GFLmhgiKo")
                    .contentType(APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("검증되지 않은 세션값으로 권한이 필요한 페이지에 요청시 실패한다.")
    @Test
    void access2() throws Exception {
        // given
        User user = User.builder()
            .email("hyukkind@naver.com")
            .name("midcon")
            .password(encoder.encrypt("1234"))
            .build();
        userRepository.save(user);

        // expected
        mockMvc.perform(
                get("/auth")
                    .header("Authorization", "1")
                    .contentType(APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}