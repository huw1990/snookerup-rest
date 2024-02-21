package com.huwdunnit.snookeruprest.exceptions;

import lombok.Getter;

@Getter
public class RoutineNotFoundException extends RuntimeException {

    private final String routineId;

    public RoutineNotFoundException(String message, String routineId) {
        super(message);
        this.routineId = routineId;
    }
}
