package com.suporte.microservico.microservico_springboot.services;

import com.suporte.microservico.microservico_springboot.model.Usuario;
import com.suporte.microservico.microservico_springboot.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveSalvarUsuarioComSucesso() {
        Usuario usuario = new Usuario(null, "NovoUsuario", "novo@email.com", "senha123");
        Usuario usuarioSalvo = new Usuario(1L, "NovoUsuario", "novo@email.com", "senhaCodificada");

        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        Usuario resultado = usuarioService.salvar(usuario);

        assertNotNull(resultado);
        assertEquals("NovoUsuario", resultado.getNome());
        assertEquals("senhaCodificada", resultado.getSenha());
        verify(usuarioRepository, times(1)).existsByEmail("novo@email.com");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoSalvarUsuarioComEmailDuplicado() {
        Usuario usuario = new Usuario(null, "UsuarioDuplicado", "duplicado@email.com", "senha123");

        when(usuarioRepository.existsByEmail("duplicado@email.com")).thenReturn(true);

        Exception excecao = assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
        assertEquals("E-mail já registrado.", excecao.getMessage());
        verify(usuarioRepository, times(1)).existsByEmail("duplicado@email.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        Usuario usuarioExistente = new Usuario(1L, "UsuarioAntigo", "antigo@email.com", "senhaAntiga");
        Usuario usuarioAtualizado = new Usuario(1L, "UsuarioNovo", "novo@email.com", "novaSenha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(false);
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaSenhaCodificada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.atualizar(1L, usuarioAtualizado);

        assertNotNull(resultado);
        assertEquals("UsuarioNovo", resultado.getNome());
        assertEquals("novo@email.com", resultado.getEmail());
        assertEquals("novaSenhaCodificada", resultado.getSenha());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode("novaSenha");
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioNaoExistente() {
        Usuario usuarioAtualizado = new Usuario(1L, "UsuarioNovo", "novo@email.com", "novaSenha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Exception excecao = assertThrows(IllegalArgumentException.class, () -> usuarioService.atualizar(1L, usuarioAtualizado));
        assertEquals("Usuário não encontrado.", excecao.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        Usuario usuarioExistente = new Usuario(1L, "UsuarioAntigo", "antigo@email.com", "senhaAntiga");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        doNothing().when(usuarioRepository).delete(usuarioExistente);

        usuarioService.deletar(1L);

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).delete(usuarioExistente);
    }

    @Test
    void deveLancarExcecaoAoDeletarUsuarioNaoExistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Exception excecao = assertThrows(IllegalArgumentException.class, () -> usuarioService.deletar(1L));
        assertEquals("Usuário não encontrado.", excecao.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }
}
