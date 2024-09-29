package com.huwdunnit.snookeruprest.exceptions;

import lombok.Getter;

@Getter
public class RoutineForScoreNotFoundException extends RuntimeException {

    private final String routineId;

    public RoutineForScoreNotFoundException(String message, String routineId) {
        super(message);
        this.routineId = routineId;
    }
}
