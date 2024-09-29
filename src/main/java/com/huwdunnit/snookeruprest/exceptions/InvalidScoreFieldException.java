package com.huwdunnit.snookeruprest.exceptions;

import lombok.Getter;

@Getter
public class InvalidScoreFieldException extends RuntimeException {

    private final String fieldName;

    public InvalidScoreFieldException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }
}
