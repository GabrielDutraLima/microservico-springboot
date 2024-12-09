package com.suporte.microservico.microservico_springboot.controller;

import com.suporte.microservico.microservico_springboot.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil; // Mock do JwtTokenUtil

    @Mock
    private PasswordEncoder passwordEncoder; // Mock do PasswordEncoder

    @InjectMocks
    private AuthController authController; // Injeção de dependências para o AuthController

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
    }



    // Teste: Verifica se o login com credenciais incorretas retorna erro 401
    @Test
    void deveRetornarErroSeCredenciaisEstiveremIncorretas() {
        when(passwordEncoder.matches("wrongPassword", "password")).thenReturn(false);

        ResponseEntity<?> response = authController.login("user", "wrongPassword");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }

    // Teste: Verifica se o login com usuário não existente retorna erro 401
    @Test
    void deveRetornarErroSeUsuarioNaoExistir() {
        when(passwordEncoder.matches("password", "nonexistentUser")).thenReturn(false);

        ResponseEntity<?> response = authController.login("nonexistentUser", "password");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }

    // Teste: Verifica se o login com credenciais vazias retorna erro 401
    @Test
    void deveRetornarErroQuandoCredenciaisEstiveremVazias() {
        when(passwordEncoder.matches("", "")).thenReturn(false);

        ResponseEntity<?> response = authController.login("", "");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }


    // Teste: Verifica se o login com o nome de usuário vazio retorna erro 401
    @Test
    void naoDeveGerarTokenParaUsuarioComUsernameVazio() {
        when(passwordEncoder.matches("", "")).thenReturn(false);

        ResponseEntity<?> response = authController.login("", "");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }

    // Teste: Verifica se o login com senha vazia retorna erro 401
    @Test
    void naoDeveGerarTokenParaUsuarioComSenhaVazia() {
        when(passwordEncoder.matches("user", "")).thenReturn(false);

        ResponseEntity<?> response = authController.login("user", "");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }

    // Teste: Verifica se o login com username correto, mas senha errada retorna erro 401
    @Test
    void deveRetornarErroQuandoSenhaEstiverErrada() {
        when(passwordEncoder.matches("wrongPassword", "correctPassword")).thenReturn(false);

        ResponseEntity<?> response = authController.login("user", "wrongPassword");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }
}
