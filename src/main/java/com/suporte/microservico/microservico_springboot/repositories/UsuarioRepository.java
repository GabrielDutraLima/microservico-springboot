package com.suporte.microservico.microservico_springboot.repositories;

import com.suporte.microservico.microservico_springboot.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método para buscar usuário por e-mail (útil para autenticação)
    Optional<Usuario> findByEmail(String email);

    // Método para verificar se um e-mail já está registrado
    boolean existsByEmail(String email);
}
