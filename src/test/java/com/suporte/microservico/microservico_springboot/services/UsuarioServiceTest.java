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
    void deveAtualizarUsuarioComEmailExistente() {
        Usuario usuarioExistente = new Usuario(1L, "UsuarioAntigo", "antigo@email.com", "senhaAntiga");
        Usuario usuarioAtualizado = new Usuario(1L, "UsuarioNovo", "novo@email.com", "novaSenha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(true);

        Exception excecao = assertThrows(IllegalArgumentException.class, () -> usuarioService.atualizar(1L, usuarioAtualizado));
        assertEquals("E-mail já registrado.", excecao.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).existsByEmail("novo@email.com");
    }

    @Test
    void deveAtualizarUsuarioSemAlterarSenha() {
        Usuario usuarioExistente = new Usuario(1L, "UsuarioAntigo", "antigo@email.com", "senhaCodificada");
        Usuario usuarioAtualizado = new Usuario(1L, "UsuarioNovo", "antigo@email.com", "senhaCodificada");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.matches("senhaCodificada", "senhaCodificada")).thenReturn(true);

        Usuario resultado = usuarioService.atualizar(1L, usuarioAtualizado);

        assertNotNull(resultado);
        assertEquals("UsuarioNovo", resultado.getNome());
        assertEquals("antigo@email.com", resultado.getEmail());
        assertEquals("senhaCodificada", resultado.getSenha());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder, times(1)).matches("senhaCodificada", "senhaCodificada");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveAtualizarUsuarioComSenhaAlterada() {
        Usuario usuarioExistente = new Usuario(1L, "UsuarioAntigo", "antigo@email.com", "senhaCodificada");
        Usuario usuarioAtualizado = new Usuario(1L, "UsuarioAtualizado", "antigo@email.com", "novaSenha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaSenhaCodificada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.atualizar(1L, usuarioAtualizado);

        assertNotNull(resultado);
        assertEquals("UsuarioAtualizado", resultado.getNome());
        assertEquals("antigo@email.com", resultado.getEmail());
        assertEquals("novaSenhaCodificada", resultado.getSenha());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("novaSenha");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
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
    void deveRetornarFalsoQuandoEmailNaoExistirNoMetodoExistsByEmail() {
        when(usuarioRepository.existsByEmail("inexistente@email.com")).thenReturn(false);

        boolean resultado = usuarioService.emailJaRegistrado("inexistente@email.com");

        assertFalse(resultado);
        verify(usuarioRepository, times(1)).existsByEmail("inexistente@email.com");
    }

    @Test
    void deveRetornarVerdadeiroQuandoEmailExistirNoMetodoExistsByEmail() {
        when(usuarioRepository.existsByEmail("existente@email.com")).thenReturn(true);

        boolean resultado = usuarioService.emailJaRegistrado("existente@email.com");

        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsByEmail("existente@email.com");
    }
}
