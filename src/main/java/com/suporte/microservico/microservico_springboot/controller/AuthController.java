package com.suporte.microservico.microservico_springboot.controller;

import com.suporte.microservico.microservico_springboot.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    // Um exemplo simples de usuários em memória (substitua por banco de dados futuramente)
    private final Map<String, String> users = new HashMap<>();

    public AuthController(JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;

        // Usuários fictícios (adicione senhas codificadas)
        users.put("user", passwordEncoder.encode("password")); // username: user, password: password
        users.put("admin", passwordEncoder.encode("admin"));   // username: admin, password: admin
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        // Verificar se o usuário existe e a senha está correta
        if (users.containsKey(username) && passwordEncoder.matches(password, users.get(username))) {
            // Gerar o token JWT
            String token = jwtTokenUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token)); // Retorna o token no formato JSON
        } else {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");
        }
    }
}
