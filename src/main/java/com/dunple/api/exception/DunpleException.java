package com.dunple.api.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class DunpleException extends RuntimeException {

    public final Map<String, String> validation = new HashMap<>();

    public DunpleException(String message) {
        super(message);
    }

    public DunpleException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
