package com.dunple.api.config.data;

import lombok.Getter;

@Getter
public class UserSession {

    private final String name;

    public UserSession(String name) {
        this.name = name;
    }
}
