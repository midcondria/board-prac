package com.dunple.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Signup {

    private String email;
    private String name;
    private String password;

    @Builder
    public Signup(String name, String password, String email) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
