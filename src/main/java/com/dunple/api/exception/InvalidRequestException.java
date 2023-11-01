package com.dunple.api.exception;

import org.springframework.http.HttpStatus;

/**
 *  status -> 400
 */
public class InvalidRequestException extends DunpleException{

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequestException() {
        super(MESSAGE);
    }

    public InvalidRequestException(String fieldName, String message) {
        super(MESSAGE);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
