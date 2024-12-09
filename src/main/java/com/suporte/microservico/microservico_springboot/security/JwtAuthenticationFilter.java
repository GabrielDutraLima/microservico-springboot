package com.suporte.microservico.microservico_springboot.security;

import jakarta.servlet.FilterChain; // Importa a interface FilterChain para manipulação da cadeia de filtros
import jakarta.servlet.ServletException; // Exceção gerada durante a execução do filtro
import jakarta.servlet.http.HttpServletRequest; // Classe que representa a requisição HTTP
import jakarta.servlet.http.HttpServletResponse; // Classe que representa a resposta HTTP
import org.springframework.security.core.context.SecurityContextHolder; // Para acessar e modificar o contexto de segurança
import org.springframework.stereotype.Component; // Define que esta classe é um componente do Spring
import org.springframework.web.filter.OncePerRequestFilter; // Filtro que será executado uma vez por requisição

import java.io.IOException; // Exceção gerada durante a execução do filtro

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil; // Instância do utilitário para trabalhar com JWT

    // Construtor para injetar a dependência de JwtTokenUtil
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil; // Inicializa o JwtTokenUtil
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extrair o cabeçalho "Authorization" da requisição
        String authHeader = request.getHeader("Authorization");

        // Verifica se o cabeçalho está presente e começa com "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extrai o token JWT removendo o prefixo "Bearer "
            String token = authHeader.substring(7);

            // Valida o token JWT utilizando o JwtTokenUtil
            if (jwtTokenUtil.validateToken(token)) {
                // Se o token for válido, extrai o nome de usuário
                String username = jwtTokenUtil.extractUsername(token);

                // Cria uma instância de JwtAuthentication com o nome de usuário
                JwtAuthentication authentication = new JwtAuthentication(username);

                // Define a autenticação no contexto de segurança do Spring
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua a execução da cadeia de filtros, permitindo que a requisição prossiga
        filterChain.doFilter(request, response);
    }
}