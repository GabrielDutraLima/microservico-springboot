package com.suporte.microservico.microservico_springboot.model;

import jakarta.persistence.*; // Importa anotações do JPA para persistência de dados
import jakarta.validation.constraints.*; // Importa anotações de validação para os campos

@Entity // Anotação que marca a classe como uma entidade JPA, ou seja, uma tabela no banco de dados
public class Usuario {

    @Id // Define que este campo será a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define que o valor será gerado automaticamente pelo banco (auto-incremento)
    private Long id;

    @NotBlank(message = "O nome é obrigatório") // Valida que o nome não pode ser vazio
    private String nome;

    @Email(message = "E-mail inválido") // Valida que o email deve ser no formato correto
    @NotBlank(message = "O e-mail é obrigatório") // Valida que o e-mail não pode ser vazio
    private String email;

    @NotBlank(message = "A senha é obrigatória") // Valida que a senha não pode ser vazia
    private String senha;

    // Construtor sem argumentos, necessário para o JPA
    public Usuario() {}

    // Construtor com argumentos, para facilitar a criação do objeto
    public Usuario(Long id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(Object o, String mail, String senha) {
    }

    // Getters e Setters para acessar e modificar os atributos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
