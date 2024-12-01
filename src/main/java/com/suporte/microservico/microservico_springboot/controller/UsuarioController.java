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
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os usuários.
     *
     * @return Lista de usuários.
     */
    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário.
     * @return O usuário correspondente ao ID ou 404 se não encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo usuário.
     *
     * @param usuario Objeto do usuário a ser criado.
     * @return O usuário criado ou 400 se o ID foi enviado incorretamente.
     */
    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        // Validar dados do usuário
        if (usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null) {
            return ResponseEntity.badRequest().build(); // Retorna 400 se os dados forem inválidos
        }

        try {
            Usuario novoUsuario = usuarioService.salvar(usuario);
            return ResponseEntity.status(201).body(novoUsuario); // Retorna 201 se o usuário for criado
        } catch (IllegalArgumentException e) {
            if ("E-mail já registrado.".equals(e.getMessage())) {
                return ResponseEntity.status(409).build(); // Retorna 409 para e-mail duplicado
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
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        if (usuario.getNome() == null || usuario.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }

        return usuarioService.buscarPorId(id)
                .map(u -> {
                    usuario.setId(id);
                    Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
                    return ResponseEntity.ok(usuarioAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deleta um usuário pelo ID.
     *
     * @param id ID do usuário a ser deletado.
     * @return 204 se o usuário foi excluído ou 404 se não encontrado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    usuarioService.deletar(id);
                    return ResponseEntity.noContent().<Void>build(); // Explicitamente define o tipo como Void
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
