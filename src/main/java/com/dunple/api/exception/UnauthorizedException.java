package com.dunple.api.exception;

import org.springframework.http.HttpStatus;

/**
 *  status -> 404
 */
public class UnauthorizedException extends DunpleException{

    private static final String MESSAGE = "인증이 필요합니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
