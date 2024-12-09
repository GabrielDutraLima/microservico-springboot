package com.suporte.microservico.microservico_springboot.security;

import io.jsonwebtoken.*; // Biblioteca para trabalhar com JWT
import io.jsonwebtoken.security.Keys; // Utilitário para gerar chaves seguras
import org.springframework.stereotype.Component; // Define esta classe como um componente do Spring

import java.security.Key; // Para manipulação de chaves de segurança
import java.util.Date; // Para manipulação de datas, principalmente para expiração do token

@Component
public class JwtTokenUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Gera uma chave secreta para assinar o JWT, usando o algoritmo HS256 (HMAC com SHA-256)

    private final long expirationTime = 86400000; // Tempo de expiração do token (1 dia em milissegundos)

    /**
     * Gera um token JWT com base no nome de usuário.
     * @param username Nome de usuário para gerar o token.
     * @return O token JWT gerado.
     */
    public String generateToken(String username) {
        return Jwts.builder() // Constrói o JWT
                .setSubject(username) // Define o "subject" como o nome de usuário
                .setIssuedAt(new Date()) // Define a data de emissão do token
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Define a data de expiração do token
                .signWith(key) // Assina o token com a chave secreta
                .compact(); // Compacta o token em uma string
    }

    /**
     * Valida se o token JWT é válido.
     * @param token Token JWT a ser validado.
     * @return true se o token for válido, false caso contrário.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() // Cria um parser para o JWT
                    .setSigningKey(key) // Define a chave usada para assinar o token
                    .build()
                    .parseClaimsJws(token); // Tenta parsear o JWT; se inválido, gera uma exceção
            return true; // Se não lançar exceções, o token é válido
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Caso contrário, o token é inválido
        }
    }

    /**
     * Extrai o nome de usuário do token JWT.
     * @param token Token JWT do qual extrair o nome de usuário.
     * @return O nome de usuário extraído do token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder() // Cria um parser para o JWT
                .setSigningKey(key) // Define a chave usada para assinar o token
                .build()
                .parseClaimsJws(token) // Tenta parsear o JWT
                .getBody() // Obtém o corpo do token
                .getSubject(); // Retorna o "subject", que é o nome de usuário
    }

    // Adicionado: Getter para a chave (apenas para testes)
    public Key getKey() {
        return key; // Retorna a chave secreta
    }
}