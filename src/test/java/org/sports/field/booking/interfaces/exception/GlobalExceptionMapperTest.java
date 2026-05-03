package org.sports.field.booking.interfaces.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.exception.InputException;
import org.sports.field.booking.interfaces.model.ErrorResponse;

import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;

class GlobalExceptionMapperTest {

    private final GlobalExceptionMapper mapper = new GlobalExceptionMapper();

    @Test
    void mapsInputExceptionToBadRequest() {
        Response response = mapper.toResponse(new InputException("Invalid input"));
        ErrorResponse body = (ErrorResponse) response.getEntity();

        assertEquals(400, response.getStatus());
        assertEquals("INPUT_ERROR", body.code);
    }

    @Test
    void mapsDatabaseExceptionToInternalServerError() {
        Response response = mapper.toResponse(new DatabaseException("Failed to query database", new RuntimeException()));
        ErrorResponse body = (ErrorResponse) response.getEntity();

        assertEquals(500, response.getStatus());
        assertEquals("DATABASE_ERROR", body.code);
    }

    @Test
    void mapsRawPersistenceExceptionToDatabaseError() {
        Response response = mapper.toResponse(new PersistenceException("connection failed"));
        ErrorResponse body = (ErrorResponse) response.getEntity();

        assertEquals(500, response.getStatus());
        assertEquals("DATABASE_ERROR", body.code);
    }
}
