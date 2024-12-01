package com.suporte.microservico.microservico_springboot.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationTest {

    @Test
    void deveCriarAutenticacaoComUsername() {
        // Cenário
        String username = "usuario_teste";

        // Ação
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação
        assertEquals(username, jwtAuthentication.getPrincipal(), "O principal deve ser o username fornecido.");
        assertNull(jwtAuthentication.getCredentials(), "As credenciais devem ser nulas.");
        assertTrue(jwtAuthentication.isAuthenticated(), "A autenticação deve estar marcada como válida.");
        assertTrue(jwtAuthentication.getAuthorities().isEmpty(), "A lista de autoridades deve estar vazia.");
    }

    @Test
    void deveRetornarUsernameCorretoAoChamarGetPrincipal() {
        // Cenário
        String username = "test_user";

        // Ação
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação
        assertEquals(username, jwtAuthentication.getPrincipal(), "O principal deve corresponder ao nome de usuário fornecido.");
    }

    @Test
    void deveRetornarNullParaCredenciais() {
        // Cenário
        String username = "user_without_credentials";

        // Ação
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação
        assertNull(jwtAuthentication.getCredentials(), "As credenciais devem ser nulas para autenticação JWT.");
    }

    @Test
    void deveConsiderarAutenticacaoComoAutenticada() {
        // Cenário
        String username = "authenticated_user";

        // Ação
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação
        assertTrue(jwtAuthentication.isAuthenticated(), "A autenticação deve estar marcada como autenticada.");
    }

    @Test
    void devePermitirFornecimentoDeAuthoritiesVazias() {
        // Cenário
        String username = "user_without_roles";

        // Ação
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação
        assertTrue(jwtAuthentication.getAuthorities().isEmpty(), "A lista de autoridades deve ser vazia.");
    }
}
