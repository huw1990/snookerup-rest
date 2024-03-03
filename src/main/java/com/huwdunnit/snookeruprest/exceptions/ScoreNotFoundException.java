package com.huwdunnit.snookeruprest.exceptions;

import lombok.Getter;

@Getter
public class ScoreNotFoundException extends RuntimeException {

    private final String scoreId;

    public ScoreNotFoundException(String message, String scoreId) {
        super(message);
        this.scoreId = scoreId;
    }
}
