package com.dunple.api.crypto;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class PlainPasswordEncoder implements PasswordEncoder {

    @Override
    public String encrypt(String rawPassword) {
        System.out.println("PlainPasswordEncoder.encryptPlain");
        return rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encryptedPassword) {
        return rawPassword.equals(encryptedPassword);
    }
}
