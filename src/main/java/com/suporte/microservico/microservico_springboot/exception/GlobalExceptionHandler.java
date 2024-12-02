package com.suporte.microservico.microservico_springboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        // Tratamento genérico para qualquer exceção
        return new ResponseEntity<>("Erro interno no servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Trata exceções de argumentos inválidos, como no caso de e-mail já registrado
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<String>> handleValidationException(BindException ex) {
        // Retorna erros de validação de forma detalhada
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(objectError -> ((FieldError) objectError).getDefaultMessage())
                .toList();
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Adicione mais manipuladores de exceção conforme necessário, como para NotFoundException, etc.
}
