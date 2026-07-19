package com.wareflow.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(

        OffsetDateTime timestamp,

        int status,

        String error,

        String code,

        String message,

        String path,

        Map<String, List<String>> validationErrors

) {
}