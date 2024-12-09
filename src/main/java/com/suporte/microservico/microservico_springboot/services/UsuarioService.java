package com.suporte.microservico.microservico_springboot.services;

import com.suporte.microservico.microservico_springboot.model.Usuario;
import com.suporte.microservico.microservico_springboot.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Construtor para injeção de dependências
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método que verifica se o e-mail já está registrado
    public boolean emailJaRegistrado(String email) {
        return usuarioRepository.existsByEmail(email); // Verifica se o e-mail já está registrado
    }

    /**
     * Lista todos os usuários.
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll(); // Retorna todos os usuários do banco de dados
    }

    /**
     * Busca um usuário pelo ID.
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id); // Retorna o usuário com o ID fornecido
    }

    /**
     * Cria um novo usuário.
     */
    public Usuario salvar(Usuario usuario) {
        if (emailJaRegistrado(usuario.getEmail())) {  // Verifica se o e-mail já está registrado
            throw new IllegalArgumentException("E-mail já registrado.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha())); // Codifica a senha
        return usuarioRepository.save(usuario); // Salva o usuário no banco de dados
    }

    /**
     * Atualiza um usuário existente.
     */
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Verifica se o e-mail foi alterado e se o novo e-mail já está registrado
        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail()) && emailJaRegistrado(usuarioAtualizado.getEmail())) {
            throw new IllegalArgumentException("E-mail já registrado.");
        }

        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());

        if (!passwordEncoder.matches(usuarioAtualizado.getSenha(), usuarioExistente.getSenha())) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha())); // Atualiza a senha
        }

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Deleta um usuário.
     */
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        usuarioRepository.delete(usuario);
    }
}
