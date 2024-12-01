package com.suporte.microservico.microservico_springboot.controller;

import com.suporte.microservico.microservico_springboot.model.Usuario;
import com.suporte.microservico.microservico_springboot.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveListarTodosOsUsuarios() {
        List<Usuario> usuarios = Arrays.asList(
                new Usuario(1L, "Usuario1", "usuario1@email.com", "senha1"),
                new Usuario(2L, "Usuario2", "usuario2@email.com", "senha2")
        );
        when(usuarioService.listarTodos()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioController.listarTodos();

        assertEquals(2, resultado.size());
        verify(usuarioService, times(1)).listarTodos();
    }

    @Test
    void deveRetornarErro500QuandoExcecaoOcorreAoListarUsuarios() {
        when(usuarioService.listarTodos()).thenThrow(new RuntimeException("Erro interno"));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> usuarioController.listarTodos());
        assertEquals("Erro interno", excecao.getMessage());
        verify(usuarioService, times(1)).listarTodos();
    }

    @Test
    void deveBuscarUsuarioPorId() {
        Usuario usuario = new Usuario(1L, "Usuario1", "usuario1@email.com", "senha1");
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<Usuario> resposta = usuarioController.buscarPorId(1L);

        assertNotNull(resposta.getBody());
        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals("Usuario1", resposta.getBody().getNome());
        verify(usuarioService, times(1)).buscarPorId(1L);
    }

    @Test
    void deveRetornar404SeUsuarioNaoExistir() {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> resposta = usuarioController.buscarPorId(1L);

        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        Usuario novoUsuario = new Usuario(null, "NovoUsuario", "novo@email.com", "senha");
        Usuario usuarioSalvo = new Usuario(1L, "NovoUsuario", "novo@email.com", "senhaCodificada");

        when(usuarioService.salvar(novoUsuario)).thenReturn(usuarioSalvo);

        ResponseEntity<Usuario> resposta = usuarioController.criar(novoUsuario);

        assertEquals(201, resposta.getStatusCodeValue());
        assertNotNull(resposta.getBody());
        assertEquals("NovoUsuario", resposta.getBody().getNome());
        verify(usuarioService, times(1)).salvar(novoUsuario);
    }

    @Test
    void deveRetornar400AoCriarUsuarioComDadosInvalidos() {
        Usuario usuarioIncompleto = new Usuario(null, null, "emailinvalido", null);

        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioIncompleto);

        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveRetornarErro409SeEmailJaExistirAoCriarUsuario() {
        Usuario usuarioDuplicado = new Usuario(null, "Usuario", "email@duplicado.com", "senha");
        when(usuarioService.salvar(usuarioDuplicado)).thenThrow(new IllegalArgumentException("E-mail já registrado."));

        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioDuplicado);

        assertEquals(409, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).salvar(usuarioDuplicado);
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        Usuario usuario = new Usuario(1L, "Usuario", "usuario@email.com", "senha");
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).deletar(1L);

        ResponseEntity<Void> resposta = usuarioController.deletar(1L);

        assertEquals(204, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, times(1)).deletar(1L);
    }

    @Test
    void naoDeveDeletarUsuarioSeNaoExistir() {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> resposta = usuarioController.deletar(1L);

        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, never()).deletar(any());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        Usuario usuarioExistente = new Usuario(1L, "Existente", "existente@email.com", "senha");
        Usuario usuarioAtualizado = new Usuario(1L, "Atualizado", "novo@email.com", "novaSenha");

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioService.atualizar(eq(1L), any(Usuario.class))).thenReturn(usuarioAtualizado);

        ResponseEntity<Usuario> resposta = usuarioController.atualizar(1L, usuarioAtualizado);

        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals("Atualizado", resposta.getBody().getNome());
        verify(usuarioService, times(1)).atualizar(eq(1L), any());
    }
}
