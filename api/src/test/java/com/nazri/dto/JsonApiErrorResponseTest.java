package com.nazri.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsonApiErrorResponseTest {

    @Test
    void testJsonApiErrorResponseStructure() {
        JsonApiErrorResponse response = new JsonApiErrorResponse("Resource not found", "NOT_FOUND", 404);

        assertNotNull(response.errors);
        assertEquals(1, response.errors.size());

        JsonApiErrorResponse.Error error = response.errors.get(0);
        assertNotNull(error.id);
        assertEquals("404", error.status);
        assertEquals("NOT_FOUND", error.code);
        assertEquals("Resource not found", error.detail);
    }
}
