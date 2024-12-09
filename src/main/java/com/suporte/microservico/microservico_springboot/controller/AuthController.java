package com.suporte.microservico.microservico_springboot.controller;

import com.suporte.microservico.microservico_springboot.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController // Define que essa classe será um controlador REST
@RequestMapping("/api/auth") // Define a rota base para autenticação
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil; // Instância de JwtTokenUtil para gerar o token JWT
    private final PasswordEncoder passwordEncoder; // Instância do PasswordEncoder para codificar e verificar senhas

    // Um exemplo simples de usuários em memória (substitua por banco de dados futuramente)
    private final Map<String, String> users = new HashMap<>(); // Mapa de usuários e senhas (simulando um banco de dados)

    // Construtor que injeta as dependências do JwtTokenUtil e PasswordEncoder
    public AuthController(JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;

        // Usuários fictícios (adicione senhas codificadas)
        users.put("user", passwordEncoder.encode("password")); // username: user, password: password
        users.put("admin", passwordEncoder.encode("admin"));   // username: admin, password: admin
    }

    // Método de login que será chamado quando um usuário tentar autenticar
    @PostMapping("/login") // Define que esse método irá responder a requisições POST na rota /login
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        // Verifica se o usuário existe e se a senha fornecida corresponde à senha codificada no mapa
        if (users.containsKey(username) && passwordEncoder.matches(password, users.get(username))) {
            // Se as credenciais estiverem corretas, gera o token JWT
            String token = jwtTokenUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token)); // Retorna o token JWT no formato JSON
        } else {
            // Se as credenciais forem inválidas, retorna um erro 401 (Unauthorized)
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");
        }
    }
}