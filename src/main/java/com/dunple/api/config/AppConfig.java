package com.dunple.api.config;

import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "midcon")
public class AppConfig {

    private final String jwtKey;

    public byte[] getJwtKey() {
        return Decoders.BASE64.decode(jwtKey);
    }
}
