package com.suporte.microservico.microservico_springboot.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationTest {

    @Test
    void deveCriarAutenticacaoComUsername() {
        // Cenário: Definindo o nome de usuário a ser utilizado para autenticação
        String username = "usuario_teste";

        // Ação: Criando o objeto JwtAuthentication com o nome de usuário fornecido
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação: Garantindo que o principal (nome de usuário) é o esperado
        assertEquals(username, jwtAuthentication.getPrincipal(), "O principal deve ser o username fornecido.");

        // Verificação: Garantindo que as credenciais devem ser nulas para autenticação JWT
        assertNull(jwtAuthentication.getCredentials(), "As credenciais devem ser nulas.");

        // Verificação: Garantindo que a autenticação é marcada como válida (true)
        assertTrue(jwtAuthentication.isAuthenticated(), "A autenticação deve estar marcada como válida.");

        // Verificação: Garantindo que não há autoridades (roles) associadas ao JWT
        assertTrue(jwtAuthentication.getAuthorities().isEmpty(), "A lista de autoridades deve estar vazia.");
    }

    @Test
    void deveRetornarUsernameCorretoAoChamarGetPrincipal() {
        // Cenário: Definindo um nome de usuário
        String username = "test_user";

        // Ação: Criando o objeto JwtAuthentication com o nome de usuário fornecido
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação: Garantindo que o método getPrincipal retorna o nome de usuário correto
        assertEquals(username, jwtAuthentication.getPrincipal(), "O principal deve corresponder ao nome de usuário fornecido.");
    }

    @Test
    void deveRetornarNullParaCredenciais() {
        // Cenário: Definindo um nome de usuário sem credenciais adicionais
        String username = "user_without_credentials";

        // Ação: Criando o objeto JwtAuthentication com o nome de usuário fornecido
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação: Garantindo que o método getCredentials retorna null, pois JWT não utiliza credenciais adicionais
        assertNull(jwtAuthentication.getCredentials(), "As credenciais devem ser nulas para autenticação JWT.");
    }

    @Test
    void deveConsiderarAutenticacaoComoAutenticada() {
        // Cenário: Definindo um nome de usuário para autenticação
        String username = "authenticated_user";

        // Ação: Criando o objeto JwtAuthentication com o nome de usuário fornecido
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação: Garantindo que a autenticação foi marcada como válida (autenticada)
        assertTrue(jwtAuthentication.isAuthenticated(), "A autenticação deve estar marcada como autenticada.");
    }

    @Test
    void devePermitirFornecimentoDeAuthoritiesVazias() {
        // Cenário: Definindo um nome de usuário sem papéis de usuário (roles)
        String username = "user_without_roles";

        // Ação: Criando o objeto JwtAuthentication com o nome de usuário fornecido
        JwtAuthentication jwtAuthentication = new JwtAuthentication(username);

        // Verificação: Garantindo que a lista de autoridades (roles) está vazia
        assertTrue(jwtAuthentication.getAuthorities().isEmpty(), "A lista de autoridades deve estar vazia.");
    }
}
