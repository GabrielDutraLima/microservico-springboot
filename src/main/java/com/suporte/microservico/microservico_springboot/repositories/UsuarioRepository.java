package com.suporte.microservico.microservico_springboot.repositories;

import com.suporte.microservico.microservico_springboot.model.Usuario; // Importa o modelo de dados Usuario
import org.springframework.data.jpa.repository.JpaRepository; // Importa a interface JpaRepository para facilitar operações com o banco de dados
import org.springframework.stereotype.Repository; // Indica que esta classe é um repositório do Spring

import java.util.Optional; // Importa a classe Optional, que é usada para representar um valor que pode ser nulo

@Repository // Marca a interface como um repositório Spring, o que indica que ela irá interagir com a camada de persistência de dados
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método para buscar usuário por e-mail (útil para autenticação)
    Optional<Usuario> findByEmail(String email);

    // Método para verificar se um e-mail já está registrado
    boolean existsByEmail(String email);
}
