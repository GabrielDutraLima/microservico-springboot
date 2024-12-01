package com.suporte.microservico.microservico_springboot.services;

import com.suporte.microservico.microservico_springboot.model.Usuario;
import com.suporte.microservico.microservico_springboot.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Define a classe como um serviço do Spring (responsável pela lógica de negócios)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository; // Repositório para acessar dados de usuários
    private final PasswordEncoder passwordEncoder; // Para codificar e verificar senhas

    // Construtor para injeção de dependência
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Lista todos os usuários.
     * @return Lista de usuários.
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll(); // Retorna todos os usuários do banco de dados
    }

    /**
     * Busca um usuário pelo ID.
     * @param id ID do usuário.
     * @return O usuário encontrado ou Optional vazio se não encontrado.
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id); // Retorna o usuário com o ID fornecido
    }

    /**
     * Cria um novo usuário.
     * @param usuario O objeto do usuário a ser criado.
     * @return O usuário criado.
     * @throws IllegalArgumentException Se o e-mail já estiver registrado.
     */
    public Usuario salvar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já registrado."); // Verifica se o e-mail já está registrado
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha())); // Codifica a senha
        return usuarioRepository.save(usuario); // Salva o usuário no banco de dados
    }

    /**
     * Atualiza um usuário existente.
     * @param id ID do usuário a ser atualizado.
     * @param usuarioAtualizado O objeto com os dados atualizados do usuário.
     * @return O usuário atualizado.
     * @throws IllegalArgumentException Se o usuário não for encontrado ou o e-mail já estiver registrado.
     */
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado.")); // Busca o usuário ou lança exceção se não encontrado

        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
            throw new IllegalArgumentException("E-mail já registrado."); // Verifica se o e-mail já está registrado
        }

        usuarioExistente.setNome(usuarioAtualizado.getNome()); // Atualiza o nome do usuário
        usuarioExistente.setEmail(usuarioAtualizado.getEmail()); // Atualiza o e-mail

        if (!passwordEncoder.matches(usuarioAtualizado.getSenha(), usuarioExistente.getSenha())) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha())); // Codifica e atualiza a senha se ela foi alterada
        }

        return usuarioRepository.save(usuarioExistente); // Salva as alterações no banco de dados
    }

    /**
     * Deleta um usuário.
     * @param id ID do usuário a ser deletado.
     * @throws IllegalArgumentException Se o usuário não for encontrado.
     */
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado.")); // Busca o usuário ou lança exceção se não encontrado
        usuarioRepository.delete(usuario); // Deleta o usuário do banco de dados
    }
}
