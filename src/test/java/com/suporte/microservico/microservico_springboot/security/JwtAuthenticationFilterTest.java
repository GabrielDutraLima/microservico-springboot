package com.suporte.microservico.microservico_springboot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenUtil);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // Limpar o contexto de segurança após cada teste
    }

    @Test
    void deveConfigurarAutenticacaoQuandoTokenValidoForFornecido() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer tokenValido");

        when(jwtTokenUtil.validateToken("tokenValido")).thenReturn(true);
        when(jwtTokenUtil.extractUsername("tokenValido")).thenReturn("usuarioTeste");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("usuarioTeste", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(jwtTokenUtil, times(1)).validateToken("tokenValido");
        verify(jwtTokenUtil, times(1)).extractUsername("tokenValido");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void naoDeveConfigurarAutenticacaoQuandoTokenInvalidoForFornecido() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer tokenInvalido");

        when(jwtTokenUtil.validateToken("tokenInvalido")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenUtil, times(1)).validateToken("tokenInvalido");
        verify(jwtTokenUtil, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void deveContinuarCadeiaDeFiltrosQuandoNaoHaToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(jwtTokenUtil, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
