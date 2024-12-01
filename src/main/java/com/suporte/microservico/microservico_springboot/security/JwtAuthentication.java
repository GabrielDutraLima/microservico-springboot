package com.suporte.microservico.microservico_springboot.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String username;

    public JwtAuthentication(String username) {
        super(null); // Sem roles ou permissões
        this.username = username;
        setAuthenticated(true); // Considera a autenticação como válida
    }

    @Override
    public Object getCredentials() {
        return null; // Sem credenciais adicionais
    }

    @Override
    public Object getPrincipal() {
        return username; // Retorna o nome de usuário
    }
}
