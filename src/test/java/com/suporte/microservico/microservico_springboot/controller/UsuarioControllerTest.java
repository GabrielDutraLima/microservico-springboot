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
    private UsuarioController usuarioController; // Injeção da classe que será testada (UsuarioController)

    @Mock
    private UsuarioService usuarioService; // Mock da dependência UsuarioService

    // Configuração do mock antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
    }

    @Test
    void deveListarTodosOsUsuarios() {
        // Prepara o cenário: cria uma lista de usuários
        List<Usuario> usuarios = Arrays.asList(
                new Usuario(1L, "Usuario1", "usuario1@email.com", "senha1"),
                new Usuario(2L, "Usuario2", "usuario2@email.com", "senha2")
        );
        when(usuarioService.listarTodos()).thenReturn(usuarios); // Quando a service listar todos, retorna a lista criada

        // Ação: chama o método a ser testado
        List<Usuario> resultado = usuarioController.listarTodos();

        // Verificação: verifica se o resultado é o esperado
        assertEquals(2, resultado.size());
        verify(usuarioService, times(1)).listarTodos(); // Verifica se o método foi chamado uma vez
    }

    @Test
    void deveRetornarErro500QuandoExcecaoOcorreAoListarUsuarios() {
        // Prepara o cenário: lança uma exceção ao chamar o serviço
        when(usuarioService.listarTodos()).thenThrow(new RuntimeException("Erro interno"));

        // Ação: tenta chamar o método e verificar se a exceção é lançada
        RuntimeException excecao = assertThrows(RuntimeException.class, () -> usuarioController.listarTodos());

        // Verificação: verifica se a exceção contém a mensagem correta
        assertEquals("Erro interno", excecao.getMessage());
        verify(usuarioService, times(1)).listarTodos();
    }

    @Test
    void deveBuscarUsuarioPorId() {
        // Prepara o cenário: cria um usuário e o busca pelo ID
        Usuario usuario = new Usuario(1L, "Usuario1", "usuario1@email.com", "senha1");
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        // Ação: chama o método de buscar usuário
        ResponseEntity<Usuario> resposta = usuarioController.buscarPorId(1L);

        // Verificação: verifica se o corpo da resposta não é nulo e o status é 200 (OK)
        assertNotNull(resposta.getBody());
        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals("Usuario1", resposta.getBody().getNome());
        verify(usuarioService, times(1)).buscarPorId(1L);
    }

    @Test
    void deveRetornar404SeUsuarioNaoExistir() {
        // Prepara o cenário: busca um usuário que não existe
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());

        // Ação: chama o método de buscar usuário
        ResponseEntity<Usuario> resposta = usuarioController.buscarPorId(1L);

        // Verificação: verifica se o status retornado é 404 (Não encontrado)
        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        // Prepara o cenário: cria um novo usuário
        Usuario novoUsuario = new Usuario(null, "NovoUsuario", "novo@email.com", "senha");
        Usuario usuarioSalvo = new Usuario(1L, "NovoUsuario", "novo@email.com", "senhaCodificada");

        // Quando o serviço for chamado, ele retorna o usuário salvo
        when(usuarioService.salvar(novoUsuario)).thenReturn(usuarioSalvo);

        // Ação: chama o método de criação de usuário
        ResponseEntity<Usuario> resposta = usuarioController.criar(novoUsuario);

        // Verificação: verifica se o status é 201 (Criado) e os dados do usuário
        assertEquals(201, resposta.getStatusCodeValue());
        assertNotNull(resposta.getBody());
        assertEquals("NovoUsuario", resposta.getBody().getNome());
        verify(usuarioService, times(1)).salvar(novoUsuario);
    }

    @Test
    void deveRetornar400AoCriarUsuarioComDadosInvalidos() {
        // Prepara o cenário: cria um usuário com dados inválidos
        Usuario usuarioIncompleto = new Usuario(null, null, "emailinvalido", null);

        // Ação: chama o método de criação de usuário
        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioIncompleto);

        // Verificação: verifica se o status retornado é 400 (Bad Request)
        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).salvar(any(Usuario.class)); // Verifica que o método salvar não foi chamado
    }

    @Test
    void deveRetornarErro409SeEmailJaExistirAoCriarUsuario() {
        // Prepara o cenário: cria um usuário com e-mail duplicado
        Usuario usuarioDuplicado = new Usuario(null, "Usuario", "email@duplicado.com", "senha");
        when(usuarioService.salvar(usuarioDuplicado)).thenThrow(new IllegalArgumentException("E-mail já registrado."));

        // Ação: chama o método de criação de usuário
        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioDuplicado);

        // Verificação: verifica se o status retornado é 409 (Conflito)
        assertEquals(409, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).salvar(usuarioDuplicado);
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        // Prepara o cenário: cria um usuário e o deleta
        Usuario usuario = new Usuario(1L, "Usuario", "usuario@email.com", "senha");
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).deletar(1L);

        // Ação: chama o método de deletar usuário
        ResponseEntity<Void> resposta = usuarioController.deletar(1L);

        // Verificação: verifica se o status retornado é 204 (Sem conteúdo)
        assertEquals(204, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, times(1)).deletar(1L);
    }

    @Test
    void naoDeveDeletarUsuarioSeNaoExistir() {
        // Prepara o cenário: busca um usuário que não existe
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());

        // Ação: chama o método de deletar usuário
        ResponseEntity<Void> resposta = usuarioController.deletar(1L);

        // Verificação: verifica se o status retornado é 404 (Não encontrado)
        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, never()).deletar(any());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        // Prepara o cenário: cria um usuário e depois o atualiza
        Usuario usuarioExistente = new Usuario(1L, "Existente", "existente@email.com", "senha");
        Usuario usuarioAtualizado = new Usuario(1L, "Atualizado", "novo@email.com", "novaSenha");

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioService.atualizar(eq(1L), any(Usuario.class))).thenReturn(usuarioAtualizado);

        // Ação: chama o método de atualização de usuário
        ResponseEntity<Usuario> resposta = usuarioController.atualizar(1L, usuarioAtualizado);

        // Verificação: verifica se o status é 200 (OK) e o nome do usuário foi atualizado
        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals("Atualizado", resposta.getBody().getNome());
        verify(usuarioService, times(1)).atualizar(eq(1L), any());
    }
}
