package com.suporte.microservico.microservico_springboot.security;

import org.springframework.security.authentication.AbstractAuthenticationToken; // Importa a classe base para tokens de autenticação
import org.springframework.security.core.GrantedAuthority; // Importa a interface para definir permissões e roles do usuário

import java.util.Collection; // Importa a coleção para as permissões/roles do usuário

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String username; // Armazena o nome de usuário do token JWT

    // Construtor que inicializa o objeto com o nome de usuário
    public JwtAuthentication(String username) {
        super(null); // Passa null porque não estamos usando roles ou permissões
        this.username = username; // Define o nome de usuário
        setAuthenticated(true); // Marca a autenticação como válida
    }

    @Override
    public Object getCredentials() {
        return null; // Retorna null, pois não há credenciais adicionais no token JWT
    }

    @Override
    public Object getPrincipal() {
        return username; // Retorna o nome de usuário como principal
    }
}
