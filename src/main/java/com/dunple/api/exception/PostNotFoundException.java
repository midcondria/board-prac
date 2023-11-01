package com.dunple.api.exception;

/**
 *  status -> 404
 */
public class PostNotFoundException extends DunpleException{

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
