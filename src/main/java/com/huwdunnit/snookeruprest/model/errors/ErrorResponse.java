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

    private String errorMessage;

    public static ErrorResponse createDuplicateValueErrorResponse() {
        return ErrorResponse.builder().errorMessage(DUPLICATE_FIELD).build();
    }
}