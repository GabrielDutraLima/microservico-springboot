package com.suporte.microservico.microservico_springboot.controller;

import com.suporte.microservico.microservico_springboot.model.Usuario;
import com.suporte.microservico.microservico_springboot.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;  // Substitua javax por jakarta
import jakarta.validation.constraints.*;  // Substitua javax por jakarta
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Validated // Validação dos dados nos métodos do controlador
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody @Valid Usuario usuario) {
        if (usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null) {
            return ResponseEntity.badRequest().build(); // Retorna 400 quando dados estão faltando
        }

        try {
            Usuario novoUsuario = usuarioService.salvar(usuario);
            return ResponseEntity.status(201).body(novoUsuario); // Retorna 201 quando o usuário é criado
        } catch (IllegalArgumentException e) {
            if ("E-mail já registrado.".equals(e.getMessage())) {
                return ResponseEntity.status(409).build(); // Retorna 409 em caso de e-mail duplicado
            }
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody @Valid Usuario usuario) {
        if (usuario.getNome() == null || usuario.getEmail() == null) {
            return ResponseEntity.badRequest().build(); // Retorna 400 quando os dados são inválidos
        }

        return usuarioService.buscarPorId(id)
                .map(u -> {
                    usuario.setId(id);
                    Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
                    return ResponseEntity.ok(usuarioAtualizado); // Retorna 200 quando o usuário é atualizado
                })
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrar o usuário
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    usuarioService.deletar(id);
                    return ResponseEntity.noContent().<Void>build(); // Retorna 204 quando o usuário é deletado
                })
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrar o usuário
    }
}