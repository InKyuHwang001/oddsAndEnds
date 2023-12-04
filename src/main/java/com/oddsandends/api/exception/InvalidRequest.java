package com.oddsandends.api.exception;

import lombok.Getter;

@Getter
public class InvalidRequest extends OddsAndEndsException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(String feildName, String message) {
        super(MESSAGE);
        addValidation(feildName, message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
