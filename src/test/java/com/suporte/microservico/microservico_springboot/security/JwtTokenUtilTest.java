package com.suporte.microservico.microservico_springboot.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    @Test
    void testGenerateAndValidateToken() {
        // Cenário: Criando uma instância do utilitário JWT
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

        // Ação: Gerando um token para o usuário "testeUsuario"
        String token = jwtTokenUtil.generateToken("testeUsuario");

        // Verificação: Garantindo que o token não seja nulo
        assertNotNull(token, "O token não deve ser nulo");

        // Ação: Validando o token gerado
        boolean isValid = jwtTokenUtil.validateToken(token);

        // Verificação: Garantindo que o token seja válido
        assertTrue(isValid, "O token deve ser válido");

        // Ação: Extraindo o nome de usuário do token
        String username = jwtTokenUtil.extractUsername(token);

        // Verificação: Garantindo que o nome de usuário extraído seja o esperado
        assertEquals("testeUsuario", username, "O username extraído deve ser 'testeUsuario'");
    }

    @Test
    void testInvalidToken() {
        // Cenário: Criando uma instância do utilitário JWT
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

        // Ação: Tentando validar um token inválido
        boolean isValid = jwtTokenUtil.validateToken("tokenInvalido");

        // Verificação: Garantindo que o token inválido seja rejeitado
        assertFalse(isValid, "Um token inválido não deve ser aceito");
    }

    @Test
    void testExpiredToken() {
        // Cenário: Criando uma instância do utilitário JWT
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        Key key = jwtTokenUtil.getKey();

        // Ação: Criando um token expirado
        String expiredToken = Jwts.builder()
                .setSubject("usuarioTeste")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000)) // Criado 2 segundos atrás
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expirado 1 segundo atrás
                .signWith(key)
                .compact();

        // Ação: Validando o token expirado
        boolean isValid = jwtTokenUtil.validateToken(expiredToken);

        // Verificação: Garantindo que o token expirado não seja considerado válido
        assertFalse(isValid, "Tokens expirados não devem ser válidos");
    }
}