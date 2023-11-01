package com.dunple.api.service;

import com.dunple.api.crypto.PasswordEncoder;
import com.dunple.api.domain.User;
import com.dunple.api.exception.AlreadyExistEmailException;
import com.dunple.api.exception.InvalidSigninInformationException;
import com.dunple.api.repository.UserRepository;
import com.dunple.api.request.LoginRequest;
import com.dunple.api.request.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Transactional
    public void signup(Signup signup) {
        Optional<User> userOptional = userRepository.findByEmail(signup.getEmail());
        if (userOptional.isPresent()) {
            throw new AlreadyExistEmailException();
        }

        String encryptedPassword = encoder.encrypt(signup.getPassword());

        User user = User.builder()
            .email(signup.getEmail())
            .name(signup.getName())
            .password(encryptedPassword)
            .build();
        userRepository.save(user);
    }

    @Transactional
    public String signin(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidSigninInformationException());


        boolean matches = encoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            throw new InvalidSigninInformationException();
        }
        return user.getName();
    }
}
