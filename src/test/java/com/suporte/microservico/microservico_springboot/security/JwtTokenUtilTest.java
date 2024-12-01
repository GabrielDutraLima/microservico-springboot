package com.suporte.microservico.microservico_springboot.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    @Test
    void testGenerateAndValidateToken() {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

        String token = jwtTokenUtil.generateToken("testeUsuario");
        assertNotNull(token, "O token não deve ser nulo");

        boolean isValid = jwtTokenUtil.validateToken(token);
        assertTrue(isValid, "O token deve ser válido");

        String username = jwtTokenUtil.extractUsername(token);
        assertEquals("testeUsuario", username, "O username extraído deve ser 'testeUsuario'");
    }

    @Test
    void testInvalidToken() {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

        boolean isValid = jwtTokenUtil.validateToken("tokenInvalido");
        assertFalse(isValid, "Um token inválido não deve ser aceito");
    }

    @Test
    void testExpiredToken() {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        Key key = jwtTokenUtil.getKey();

        String expiredToken = Jwts.builder()
                .setSubject("usuarioTeste")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000)) // Criado 2 segundos atrás
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expirado 1 segundo atrás
                .signWith(key)
                .compact();

        boolean isValid = jwtTokenUtil.validateToken(expiredToken);
        assertFalse(isValid, "Tokens expirados não devem ser válidos");
    }
}
