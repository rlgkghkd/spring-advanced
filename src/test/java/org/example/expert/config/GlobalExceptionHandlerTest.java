package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

class GlobalExceptionHandlerTest {

    @Mock
    InvalidRequestException invalidRequestException;
    @Mock
    AuthException authException;
    @Mock
    ServerException serverException;
    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void invalidRequestExceptionException() {
        given(invalidRequestException.getMessage()).willReturn("test");
        ResponseEntity<Map<String, Object>> returnEntity = globalExceptionHandler.invalidRequestExceptionException(invalidRequestException);
        assertEquals(HttpStatus.BAD_REQUEST,returnEntity.getStatusCode());
        assertEquals("test",returnEntity.getBody().get("message"));
    }

    @Test
    void handleAuthException() {
        given(authException.getMessage()).willReturn("test1");
        ResponseEntity<Map<String, Object>> returnEntity = globalExceptionHandler.handleAuthException(authException);
        assertEquals(HttpStatus.UNAUTHORIZED,returnEntity.getStatusCode());
        assertEquals("test1",returnEntity.getBody().get("message"));
    }

    @Test
    void handleServerException() {
        given(serverException.getMessage()).willReturn("test2");
        ResponseEntity<Map<String, Object>> returnEntity = globalExceptionHandler.handleServerException(serverException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,returnEntity.getStatusCode());
        assertEquals("test2",returnEntity.getBody().get("message"));
    }
}