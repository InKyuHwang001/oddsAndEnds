package com.oddsandends.api.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class OddsAndEndsException extends RuntimeException{

    public final Map<String, String> validation = new HashMap<>();

    public OddsAndEndsException(String message) {
        super(message);
    }

    public OddsAndEndsException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String feildName, String message){
        validation.put(feildName, message);
    }
}
