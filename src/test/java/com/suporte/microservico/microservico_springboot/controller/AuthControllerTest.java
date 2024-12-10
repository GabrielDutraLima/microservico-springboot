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


    @Test
    void deveRetornarErroQuandoUsernameContiverEspacosOuFormatoIncorreto() {
        // Cenário: Nome de usuário com espaços no início ou no fim
        String invalidUsername = "  user  ";
        String password = "correctPassword";

        // Mock para garantir que mesmo que a senha seja correta, o nome de usuário é inválido
        when(passwordEncoder.matches(password, password)).thenReturn(false);

        // Ação: Realiza o login com o nome de usuário mal formatado
        ResponseEntity<?> response = authController.login(invalidUsername, password);

        // Verificação: Espera-se um status 401 (Unauthorized) e mensagem de erro
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());

        // Verifica se nenhum token foi gerado e se a senha não foi validada
        verify(jwtTokenUtil, never()).generateToken(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    @Test
    void deveRetornarErroQuandoSenhaContiverEspacos() {
        // Cenário: Senha com espaços no início ou no fim
        String username = "user";
        String invalidPassword = "  password  ";

        // Mock para garantir que a senha com espaços não seja aceita
        when(passwordEncoder.matches(invalidPassword.trim(), "password")).thenReturn(false);

        // Ação: Realiza o login com a senha mal formatada
        ResponseEntity<?> response = authController.login(username, invalidPassword);

        // Verificação: Espera-se um status 401 (Unauthorized) e mensagem de erro
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }

    @Test
    void deveRetornarErroQuandoUsernameForNulo() {
        // Cenário: Nome de usuário nulo
        String password = "password";

        // Ação: Realiza o login com o nome de usuário nulo
        ResponseEntity<?> response = authController.login(null, password);

        // Verificação: Espera-se um status 401 (Unauthorized) e mensagem de erro
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }

    @Test
    void deveRetornarErroQuandoSenhaForNula() {
        // Cenário: Senha nula
        String username = "user";

        // Ação: Realiza o login com a senha nula
        ResponseEntity<?> response = authController.login(username, null);

        // Verificação: Espera-se um status 401 (Unauthorized) e mensagem de erro
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }

    @Test
    void naoDeveValidarCredenciaisQuandoAmbosUsernameESenhaForemNulos() {
        // Cenário: Nome de usuário e senha nulos
        ResponseEntity<?> response = authController.login(null, null);

        // Verificação: Espera-se um status 401 (Unauthorized) e mensagem de erro
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", response.getBody());

        // Verifica que nenhum método adicional foi chamado
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenUtil, never()).generateToken(anyString());
    }
    @Test
    void deveFalharAoGerarTokenComSenhaErrada() {
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        ResponseEntity<?> resposta = authController.login("usuario", "wrongPassword");

        assertEquals(401, resposta.getStatusCodeValue());
        assertEquals("Usuário ou senha inválidos", resposta.getBody());
    }



}
