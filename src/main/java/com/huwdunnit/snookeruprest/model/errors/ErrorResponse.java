package com.huwdunnit.snookeruprest.model.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class for mapping error responses.
 *
 * @author Huwdunnit
 */
@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    public static final String DUPLICATE_FIELD = "Duplicate value error";

    public static final String USER_NOT_FOUND = "User not found";

    public static final String ROUTINE_NOT_FOUND = "Routine not found";

    public static final String SCORE_NOT_FOUND = "Score not found";

    private String errorMessage;

    public static ErrorResponse createDuplicateValueErrorResponse() {
        return ErrorResponse.builder().errorMessage(DUPLICATE_FIELD).build();
    }

    public static ErrorResponse createUserNotFoundErrorResponse() {
        return ErrorResponse.builder().errorMessage(USER_NOT_FOUND).build();
    }

    public static ErrorResponse createRoutineNotFoundErrorResponse() {
        return ErrorResponse.builder().errorMessage(ROUTINE_NOT_FOUND).build();
    }

    public static ErrorResponse createScoreNotFoundErrorResponse() {
        return ErrorResponse.builder().errorMessage(SCORE_NOT_FOUND).build();
    }
}