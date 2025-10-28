package com.nazri.dto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class JsonApiErrorResponse {
    public final List<Error> errors;

    public JsonApiErrorResponse(String detail, String code, int status) {
        this.errors = Collections.singletonList(new Error(detail, code, status));
    }

    public static class Error {
        public final String id;
        public final String status;
        public final String code;
        public final String detail;

        public Error(String detail, String code, int status) {
            this.id = UUID.randomUUID().toString();
            this.status = String.valueOf(status);
            this.code = code;
            this.detail = detail;
        }
    }
}
