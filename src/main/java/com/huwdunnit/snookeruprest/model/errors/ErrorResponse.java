package com.huwdunnit.snookeruprest.model.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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

    public static final String ROUTINE_IN_SCORE_DOESNT_EXIST = "Invalid routine ID";

    public static final String INVALID_SCORE_FIELD = "Invalid field for routine";

    public static final String FIELD_NAME = "field";

    /** The main, high-level error message for the user. */
    private String errorMessage;

    /**
     * A map of specific details related to the error. Each error will have keys specific to that error (if context
     * is required).
     */
    private Map<String, String> context;

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

    public static ErrorResponse createRoutineForScoreNotFoundErrorResponse() {
        return ErrorResponse.builder().errorMessage(ROUTINE_IN_SCORE_DOESNT_EXIST).build();
    }

    public static ErrorResponse createInvalidScoreFieldErrorResponse(String fieldName) {
        return ErrorResponse.builder()
                .errorMessage(INVALID_SCORE_FIELD)
                .context(Map.of(FIELD_NAME, fieldName))
                .build();
    }
}