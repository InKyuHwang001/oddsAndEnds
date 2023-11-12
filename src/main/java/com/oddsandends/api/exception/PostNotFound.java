package com.oddsandends.api.exception;

public class PostNotFound extends OddsAndEndsException {

    private static final String MESSAGE = "존재하지 않는 글 입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
