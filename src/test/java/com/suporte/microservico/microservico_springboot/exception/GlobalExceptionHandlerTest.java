package com.suporte.microservico.microservico_springboot.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleException() {
        Exception ex = new Exception("Erro interno");
        ResponseEntity<String> response = handler.handleException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Erro interno no servidor"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("E-mail já registrado.");
        ResponseEntity<String> response = handler.handleIllegalArgumentException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("E-mail já registrado.", response.getBody());
    }

    @Test
    void testHandleValidationException() {
        // Preparar o BindingResult e os erros de validação
        BindingResult bindingResult = new BindException(new Object(), "usuario");
        bindingResult.addError(new FieldError("usuario", "nome", "O nome é obrigatório"));

        ResponseEntity<List<String>> response = handler.handleValidationException(new BindException(bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        List<String> errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.contains("O nome é obrigatório"));
    }
}
