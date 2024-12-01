package com.suporte.microservico.microservico_springboot.controller;

import com.suporte.microservico.microservico_springboot.model.Usuario;
import com.suporte.microservico.microservico_springboot.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operações relacionadas aos usuários.
 */
@RestController // Define que essa classe será um controlador REST
@RequestMapping("/api/usuarios") // Define a URL base para as rotas de usuários
public class UsuarioController {

    @Autowired // Injeta a dependência do UsuarioService
    private UsuarioService usuarioService;

    /**
     * Lista todos os usuários.
     *
     * @return Lista de usuários.
     */
    @GetMapping // Mapeia a requisição GET para listar todos os usuários
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos(); // Retorna todos os usuários da base de dados
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário.
     * @return O usuário correspondente ao ID ou 404 se não encontrado.
     */
    @GetMapping("/{id}") // Mapeia a requisição GET para buscar um usuário pelo ID
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        // Tenta encontrar o usuário pelo ID. Se encontrado, retorna o usuário com status 200.
        // Caso contrário, retorna status 404.
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok) // Se o usuário for encontrado, retorna com status 200
                .orElse(ResponseEntity.notFound().build()); // Caso não encontre, retorna status 404
    }

    /**
     * Cria um novo usuário.
     *
     * @param usuario Objeto do usuário a ser criado.
     * @return O usuário criado ou 400 se o ID foi enviado incorretamente.
     */
    @PostMapping // Mapeia a requisição POST para criar um novo usuário
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        // Valida os dados do usuário antes de tentar salvar
        if (usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null) {
            return ResponseEntity.badRequest().build(); // Retorna status 400 se os dados forem inválidos
        }

        try {
            Usuario novoUsuario = usuarioService.salvar(usuario); // Tenta salvar o novo usuário
            return ResponseEntity.status(201).body(novoUsuario); // Retorna o usuário criado com status 201
        } catch (IllegalArgumentException e) {
            if ("E-mail já registrado.".equals(e.getMessage())) {
                return ResponseEntity.status(409).build(); // Retorna status 409 se o e-mail já estiver registrado
            }
            throw e; // Repassa a exceção se for diferente
        }
    }

    /**
     * Atualiza um usuário existente.
     *
     * @param id      ID do usuário a ser atualizado.
     * @param usuario Objeto do usuário com as atualizações.
     * @return O usuário atualizado ou 404 se não encontrado.
     */
    @PutMapping("/{id}") // Mapeia a requisição PUT para atualizar um usuário pelo ID
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        // Verifica se os dados essenciais do usuário estão preenchidos
        if (usuario.getNome() == null || usuario.getEmail() == null) {
            return ResponseEntity.badRequest().build(); // Retorna status 400 se os dados forem inválidos
        }

        // Tenta encontrar o usuário pelo ID. Se encontrado, realiza a atualização.
        return usuarioService.buscarPorId(id)
                .map(u -> {
                    usuario.setId(id); // Atualiza o ID do usuário
                    Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario); // Atualiza o usuário
                    return ResponseEntity.ok(usuarioAtualizado); // Retorna o usuário atualizado com status 200
                })
                .orElse(ResponseEntity.notFound().build()); // Caso não encontre, retorna status 404
    }

    /**
     * Deleta um usuário pelo ID.
     *
     * @param id ID do usuário a ser deletado.
     * @return 204 se o usuário foi excluído ou 404 se não encontrado.
     */
    @DeleteMapping("/{id}") // Mapeia a requisição DELETE para excluir um usuário pelo ID
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Tenta encontrar o usuário pelo ID. Se encontrado, realiza a exclusão.
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    usuarioService.deletar(id); // Deleta o usuário
                    return ResponseEntity.noContent().<Void>build(); // Retorna status 204 (Sem conteúdo)
                })
                .orElse(ResponseEntity.notFound().build()); // Caso não encontre, retorna status 404
    }

}
