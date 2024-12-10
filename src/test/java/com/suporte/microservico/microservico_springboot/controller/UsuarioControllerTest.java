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

    // Teste para listar todos os usuários
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

    // Teste para verificar exceções ao listar usuários
    @Test
    void deveRetornarErro500QuandoExcecaoOcorreAoListarUsuarios() {
        when(usuarioService.listarTodos()).thenThrow(new RuntimeException("Erro interno"));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> usuarioController.listarTodos());

        assertEquals("Erro interno", excecao.getMessage());
        verify(usuarioService, times(1)).listarTodos();
    }

    // Teste para verificar se campos obrigatórios estão nulos ao criar um usuário
    @Test
    void deveRetornarBadRequestQuandoNomeEmailOuSenhaEstiveremNulos() {
        Usuario usuarioInvalido = new Usuario(null, "email@dominio.com", "senha");
        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioInvalido);
        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).salvar(any(Usuario.class));

        usuarioInvalido = new Usuario("Nome", null, "senha");
        resposta = usuarioController.criar(usuarioInvalido);
        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).salvar(any(Usuario.class));

        usuarioInvalido = new Usuario("Nome", "email@dominio.com", null);
        resposta = usuarioController.criar(usuarioInvalido);
        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).salvar(any(Usuario.class));
    }

    // Teste para verificar quando o email já existe
    @Test
    void deveRetornarConflictQuandoEmailJaEstiverRegistrado() {
        Usuario usuarioComEmailExistente = new Usuario(null, "Nome", "email@dominio.com", "senha");
        when(usuarioService.salvar(usuarioComEmailExistente)).thenThrow(new IllegalArgumentException("E-mail já registrado."));

        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioComEmailExistente);
        assertEquals(409, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).salvar(usuarioComEmailExistente);
    }

    // Teste para criar um usuário com sucesso
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

    // Teste para buscar um usuário por ID que existe
    @Test
    void deveRetornarOkQuandoUsuarioExistir() {
        Usuario usuario = new Usuario(1L, "Nome", "email@dominio.com", "senha");
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<Usuario> resposta = usuarioController.buscarPorId(1L);

        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals("Nome", resposta.getBody().getNome());
        verify(usuarioService, times(1)).buscarPorId(1L);
    }

    // Teste para buscar um usuário por ID que não existe
    @Test
    void deveRetornarNotFoundQuandoUsuarioNaoExistir() {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> resposta = usuarioController.buscarPorId(1L);

        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
    }

    // Teste para atualizar um usuário com sucesso
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

    @Test
    void deveRetornarBadRequestQuandoAtualizarUsuarioComCamposObrigatoriosNulos() {
        Usuario usuarioAtualizado = new Usuario(1L, null, null, "novaSenha");

        ResponseEntity<Usuario> resposta = usuarioController.atualizar(1L, usuarioAtualizado);

        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).atualizar(anyLong(), any());
    }
    @Test
    void deveRetornarErroQuandoExcluirUsuarioComIdNulo() {
        ResponseEntity<Void> resposta = usuarioController.deletar(null);

        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, never()).deletar(any());
    }



    // Teste para falha ao tentar atualizar um usuário que não existe
    @Test
    void naoDeveAtualizarUsuarioQuandoNaoExistir() {
        Usuario usuarioAtualizado = new Usuario(1L, "Atualizado", "novo@email.com", "novaSenha");

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> resposta = usuarioController.atualizar(1L, usuarioAtualizado);

        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, never()).atualizar(anyLong(), any());
    }

    // Teste para deletar um usuário com sucesso
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

    // Teste para não deletar um usuário se não existir
    @Test
    void naoDeveDeletarUsuarioSeNaoExistir() {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> resposta = usuarioController.deletar(1L);

        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, never()).deletar(any());
    }

    // Teste para verificar se o serviço retorna erro ao tentar criar um usuário com e-mail nulo
    @Test
    void deveRetornarBadRequestQuandoEmailForNulo() {
        Usuario usuarioComEmailNulo = new Usuario(null, null, "senha");
        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioComEmailNulo);
        assertEquals(400, resposta.getStatusCodeValue());
    }

    // Teste para verificar se criar usuário com e-mail já existente retorna erro 409
    @Test
    void deveRetornarConflictAoCriarUsuarioComEmailJaExistente() {
        Usuario usuarioDuplicado = new Usuario(null, "UsuarioDuplicado", "duplicado@email.com", "senha");
        when(usuarioService.salvar(usuarioDuplicado)).thenThrow(new IllegalArgumentException("E-mail já registrado."));

        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioDuplicado);

        assertEquals(409, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).salvar(usuarioDuplicado);
    }

    // Teste para verificar listagem de usuários quando não há registros
    @Test
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        when(usuarioService.listarTodos()).thenReturn(List.of());

        List<Usuario> resultado = usuarioController.listarTodos();

        assertTrue(resultado.isEmpty());
        verify(usuarioService, times(1)).listarTodos();
    }

    // Teste para tentar excluir usuário com ID inválido (negativo)
    @Test
    void deveRetornarNotFoundAoExcluirUsuarioComIdInvalido() {
        ResponseEntity<Void> resposta = usuarioController.deletar(-1L);

        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, never()).deletar(any());
    }

    // Teste para tentar excluir usuário quando o banco de dados estiver indisponível
    @Test
    void deveLancarExcecaoAoTentarExcluirComErroNoBanco() {
        Usuario usuarioExistente = new Usuario(1L, "Usuario", "usuario@email.com", "senha");
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
        doThrow(new RuntimeException("Erro ao acessar o banco de dados")).when(usuarioService).deletar(1L);

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> usuarioController.deletar(1L));

        assertEquals("Erro ao acessar o banco de dados", excecao.getMessage());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, times(1)).deletar(1L);
    }



    // Teste para atualizar usuário com nome válido, mas e-mail nulo
    @Test
    void deveRetornarBadRequestAoAtualizarUsuarioComEmailNulo() {
        Usuario usuarioAtualizado = new Usuario(1L, "NomeValido", null, "novaSenha");

        ResponseEntity<Usuario> resposta = usuarioController.atualizar(1L, usuarioAtualizado);

        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).atualizar(anyLong(), any());
    }

    @Test
    void deveRetornarBadRequestAoCriarUsuarioComNomeValidoESenhaNula() {
        Usuario usuarioParcial = new Usuario(null, "NomeValido", null, null);

        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioParcial);

        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).salvar(any());
    }
    @Test
    void deveRetornarBadRequestAoAtualizarUsuarioComTodosOsCamposNulos() {
        Usuario usuarioAtualizado = new Usuario(1L, null, null, null);

        ResponseEntity<Usuario> resposta = usuarioController.atualizar(1L, usuarioAtualizado);

        assertEquals(400, resposta.getStatusCodeValue());
        verify(usuarioService, never()).atualizar(anyLong(), any());
    }

    @Test
    void deveLancarExcecaoQuandoErroNaBuscaParaExcluirUsuario() {
        when(usuarioService.buscarPorId(1L)).thenThrow(new RuntimeException("Erro ao buscar usuário"));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> usuarioController.deletar(1L));

        assertEquals("Erro ao buscar usuário", excecao.getMessage());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, never()).deletar(any());
    }
    @Test
    void deveLancarExcecaoAoBuscarUsuarioComErroNoBanco() {
        when(usuarioService.buscarPorId(1L)).thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> usuarioController.buscarPorId(1L));

        assertEquals("Erro ao acessar o banco de dados", excecao.getMessage());
        verify(usuarioService, times(1)).buscarPorId(1L);
    }
    @Test
    void deveAtualizarUsuarioComDadosValidos() {
        // Cenário: usuário existente com novos dados válidos
        Usuario usuarioExistente = new Usuario(1L, "Usuário Atual", "email@atual.com", "senha");
        Usuario usuarioAtualizado = new Usuario(1L, "Novo Nome", "novo@email.com", "novaSenha");

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioService.atualizar(eq(1L), any(Usuario.class))).thenReturn(usuarioAtualizado);

        // Ação: atualização do usuário
        ResponseEntity<Usuario> response = usuarioController.atualizar(1L, usuarioAtualizado);

        // Verificação
        assertEquals(200, response.getStatusCodeValue()); // Status HTTP 200 (OK)
        assertNotNull(response.getBody());
        assertEquals("Novo Nome", response.getBody().getNome());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, times(1)).atualizar(eq(1L), any(Usuario.class));
    }
    @Test
    void deveAtualizarUsuarioSemAlterarDados() {
        Usuario usuarioExistente = new Usuario(1L, "Usuário Atual", "email@atual.com", "senhaCodificada");
        Usuario usuarioSemAlteracao = new Usuario(1L, "Usuário Atual", "email@atual.com", "senhaCodificada");

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioService.atualizar(eq(1L), any(Usuario.class))).thenReturn(usuarioSemAlteracao);

        ResponseEntity<Usuario> response = usuarioController.atualizar(1L, usuarioSemAlteracao);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Usuário Atual", response.getBody().getNome());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, times(1)).atualizar(eq(1L), any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComErroGenerico() {
        Usuario usuarioAtualizado = new Usuario(1L, "Usuário Atualizado", "email@novo.com", "novaSenha");

        when(usuarioService.buscarPorId(1L)).thenThrow(new RuntimeException("Erro inesperado"));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> usuarioController.atualizar(1L, usuarioAtualizado));

        assertEquals("Erro inesperado", excecao.getMessage());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, never()).atualizar(anyLong(), any());
    }

    @Test
    void deveAtualizarUsuarioComSenhaAlteradaSomente() {
        Usuario usuarioExistente = new Usuario(1L, "Usuário Atual", "email@atual.com", "senhaCodificada");
        Usuario usuarioSenhaAlterada = new Usuario(1L, "Usuário Atual", "email@atual.com", "novaSenha");

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioService.atualizar(eq(1L), any(Usuario.class))).thenReturn(usuarioSenhaAlterada);

        ResponseEntity<Usuario> response = usuarioController.atualizar(1L, usuarioSenhaAlterada);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Usuário Atual", response.getBody().getNome());
        assertEquals("novaSenha", response.getBody().getSenha());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, times(1)).atualizar(eq(1L), any(Usuario.class));
    }
    @Test
    void deveLancarExcecaoQuandoEmailDuplicadoNaAtualizacao() {
        Usuario usuarioExistente = new Usuario(1L, "UsuarioExistente", "existente@email.com", "senha");
        Usuario usuarioAtualizado = new Usuario(1L, "UsuarioAtualizado", "existente@email.com", "novaSenha");

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioService.atualizar(1L, usuarioAtualizado)).thenThrow(new IllegalArgumentException("E-mail já registrado."));

        Exception excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioController.atualizar(1L, usuarioAtualizado);
        });

        assertEquals("E-mail já registrado.", excecao.getMessage());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, times(1)).atualizar(1L, usuarioAtualizado);
    }
    @Test
    void deveRetornarErroQuandoCriarComEmailDuplicado() {
        Usuario usuarioComEmailExistente = new Usuario(null, "Usuario", "email@existente.com", "senha");

        when(usuarioService.salvar(usuarioComEmailExistente)).thenThrow(new IllegalArgumentException("E-mail já registrado."));

        ResponseEntity<Usuario> resposta = usuarioController.criar(usuarioComEmailExistente);

        assertEquals(409, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).salvar(usuarioComEmailExistente);
    }

    @Test
    void deveRetornarErroQuandoDeletarUsuarioInexistente() {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> resposta = usuarioController.deletar(1L);

        assertEquals(404, resposta.getStatusCodeValue());
        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, never()).deletar(anyLong());
    }
    @Test
    void deveRetornarErroQuandoServicoLancarExcecaoInesperada() {
        Usuario usuario = new Usuario(null, "Nome", "email@valido.com", "senha123");
        when(usuarioService.salvar(usuario)).thenThrow(new RuntimeException("Erro inesperado"));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            usuarioController.criar(usuario);
        });

        assertEquals("Erro inesperado", excecao.getMessage());
        verify(usuarioService, times(1)).salvar(usuario);
    }


    @Test
    void deveRetornarErroQuandoServicoRetornarNulo() {
        // Simula que o serviço retorna nulo
        when(usuarioService.salvar(any(Usuario.class))).thenReturn(null);

        // Executa a ação
        ResponseEntity<Usuario> resposta = usuarioController.criar(new Usuario("Nome", "email@dominio.com", "senha"));

        // Verifica se retorna 500
        assertEquals(400, resposta.getStatusCodeValue());
    }


    @Test
    void deveCriarUsuarioComTodosOsCamposValidos() {
        Usuario usuario = new Usuario(null, "Usuario Valido", "email@valido.com", "senha123");
        Usuario usuarioSalvo = new Usuario(1L, "Usuario Valido", "email@valido.com", "senha123");

        when(usuarioService.salvar(usuario)).thenReturn(usuarioSalvo);

        ResponseEntity<Usuario> resposta = usuarioController.criar(usuario);

        assertEquals(201, resposta.getStatusCodeValue());
        assertNotNull(resposta.getBody());
        assertEquals("Usuario Valido", resposta.getBody().getNome());
        verify(usuarioService, times(1)).salvar(usuario);
    }

}