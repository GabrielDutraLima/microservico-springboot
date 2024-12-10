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
    private JwtTokenUtil jwtTokenUtil; // Mock do JwtTokenUtil

    @Mock
    private FilterChain filterChain; // Mock da FilterChain, usada para continuar o fluxo de requisições

    private JwtAuthenticationFilter jwtAuthenticationFilter; // A classe que estamos testando

    // Configuração dos mocks antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenUtil); // Cria o objeto a ser testado
    }

    // Limpeza do contexto de segurança após cada teste
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // Limpa o contexto de segurança
    }

    @Test
    void deveConfigurarAutenticacaoQuandoTokenValidoForFornecido() throws ServletException, IOException {
        // Prepara o cenário: cria uma requisição com token válido
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer tokenValido");

        // Quando o token é validado, o nome de usuário "usuarioTeste" é extraído
        when(jwtTokenUtil.validateToken("tokenValido")).thenReturn(true);
        when(jwtTokenUtil.extractUsername("tokenValido")).thenReturn("usuarioTeste");

        // Ação: chama o filtro de autenticação
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificação: o contexto de segurança deve ter a autenticação configurada com o nome de usuário
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("usuarioTeste", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(jwtTokenUtil, times(1)).validateToken("tokenValido"); // Verifica se o método validateToken foi chamado
        verify(jwtTokenUtil, times(1)).extractUsername("tokenValido"); // Verifica se o método extractUsername foi chamado
        verify(filterChain, times(1)).doFilter(request, response); // Verifica se o filtro foi continuado
    }

    @Test
    void naoDeveConfigurarAutenticacaoQuandoTokenInvalidoForFornecido() throws ServletException, IOException {
        // Prepara o cenário: cria uma requisição com token inválido
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer tokenInvalido");

        // Quando o token é invalidado
        when(jwtTokenUtil.validateToken("tokenInvalido")).thenReturn(false);

        // Ação: chama o filtro de autenticação
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificação: o contexto de segurança não deve ter a autenticação configurada
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenUtil, times(1)).validateToken("tokenInvalido"); // Verifica se o método validateToken foi chamado
        verify(jwtTokenUtil, never()).extractUsername(anyString()); // Não deve tentar extrair o nome de usuário
        verify(filterChain, times(1)).doFilter(request, response); // Verifica se o filtro foi continuado
    }

    @Test
    void deveContinuarCadeiaDeFiltrosQuandoNaoHaToken() throws ServletException, IOException {
        // Prepara o cenário: cria uma requisição sem token no cabeçalho
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Ação: chama o filtro de autenticação
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificação: o contexto de segurança não deve ter a autenticação configurada
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenUtil, never()).validateToken(anyString()); // Não deve validar nenhum token
        verify(jwtTokenUtil, never()).extractUsername(anyString()); // Não deve tentar extrair o nome de usuário
        verify(filterChain, times(1)).doFilter(request, response); // Verifica se o filtro foi continuado
    }
    @Test
    void deveLancarExcecaoQuandoTokenInvalidoGeraErroInterno() throws ServletException, IOException {
        // Prepara o cenário: cria uma requisição com token inválido que gera exceção
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer tokenErro");

        // Quando o método de validação lança uma exceção
        when(jwtTokenUtil.validateToken("tokenErro")).thenThrow(new RuntimeException("Erro interno ao validar token"));

        // Ação: chama o filtro de autenticação e verifica se a exceção é capturada corretamente
        assertThrows(RuntimeException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));

        // Verificação: nenhum contexto de autenticação deve ser configurado
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenUtil, times(1)).validateToken("tokenErro");
        verify(filterChain, never()).doFilter(request, response); // O filtro não deve continuar
    }

    @Test
    void deveConfigurarAutenticacaoQuandoTokenNaoContemBearer() throws ServletException, IOException {
        // Prepara o cenário: cria uma requisição com um token sem o prefixo "Bearer"
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "tokenSemBearer");

        // Nenhuma validação deve ocorrer, pois o token é inválido
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificação: o contexto de segurança não deve ter a autenticação configurada
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(jwtTokenUtil, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response); // O filtro deve continuar
    }

    @Test
    void deveIgnorarAutenticacaoQuandoHeaderAuthorizationEstaVazio() throws ServletException, IOException {
        // Prepara o cenário: cria uma requisição sem cabeçalho de autorização
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Ação: chama o filtro de autenticação
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificação: o contexto de segurança não deve ter a autenticação configurada
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(jwtTokenUtil, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response); // O filtro deve continuar
    }



}