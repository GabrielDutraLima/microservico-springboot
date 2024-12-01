package com.suporte.microservico.microservico_springboot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extrair o cabeçalho "Authorization"
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extrair o token JWT (remover "Bearer ")
            String token = authHeader.substring(7);

            // Validar o token JWT
            if (jwtTokenUtil.validateToken(token)) {
                // Extrair o username do token
                String username = jwtTokenUtil.extractUsername(token);

                // Configurar a autenticação no contexto de segurança
                JwtAuthentication authentication = new JwtAuthentication(username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuar a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
