
# Microserviço de Suporte

Este projeto é um microserviço desenvolvido utilizando o Spring Boot. O serviço permite o gerenciamento de usuários, incluindo criação, atualização, listagem e exclusão de usuários. O projeto usa JWT (JSON Web Token) para autenticação, além de um banco de dados para armazenamento de informações dos usuários.

---

## Tecnologias Utilizadas

- **Spring Boot**: Framework principal para a construção do microserviço.
- **Spring Security**: Utilizado para autenticação e autorização com JWT.
- **JPA (Java Persistence API)**: Para interação com o banco de dados.
- **H2 Database**: Banco de dados em memória para persistência de dados durante o desenvolvimento.
- **JUnit e Mockito**: Para testes unitários e mocks das dependências.
- **Maven**: Gerenciador de dependências e build do projeto.

---

## Funcionalidades

### Criação de Usuários
- **Endpoint**: `POST /api/usuarios`
- Cria novos usuários. Verifica se os dados obrigatórios estão presentes e registrados.

### Listagem de Usuários
- **Endpoint**: `GET /api/usuarios`
- Lista todos os usuários cadastrados.

### Busca de Usuário por ID
- **Endpoint**: `GET /api/usuarios/{id}`
- Retorna um usuário específico pelo ID.

### Atualização de Usuário
- **Endpoint**: `PUT /api/usuarios/{id}`
- Atualiza os dados de um usuário existente.

### Exclusão de Usuário
- **Endpoint**: `DELETE /api/usuarios/{id}`
- Exclui um usuário pelo ID.

### Validação de Dados
- Validações de entrada utilizando Jakarta Validation para verificar campos obrigatórios, como nome, e-mail e senha.

---

## Estrutura do Projeto

1. **Controller**: O controlador `UsuarioController` gerencia as interações HTTP.
2. **Service**: A classe `UsuarioService` contém a lógica de negócios.
3. **Repository**: A interface `UsuarioRepository` estende `JpaRepository` para operações com o banco de dados.
4. **Model**: A classe `Usuario` representa a entidade de usuário.
5. **Testes**: Testes unitários com JUnit e Mockito para validação do código.

---

## Requisitos para Execução

- **Java JDK 17 ou superior**: Necessário para executar o Spring Boot.
- **Maven**: Gerenciador de dependências.
- **IDE recomendada**: IntelliJ IDEA, Eclipse ou outra de sua preferência.

---

## Como Rodar o Projeto

1. **Clone o repositório**
   ```bash
   git clone https://github.com/usuario/repo.git
   cd repo
   ```

2. **Instale as dependências**
   ```bash
   mvn clean install
   ```

3. **Execute o projeto**
   ```bash
   mvn spring-boot:run
   ```
   O servidor será iniciado em: `http://localhost:8080/api/usuarios`

4. **Rodar os testes**
   ```bash
   mvn test
   ```

---

## Endpoints da API

### 1. Criar Usuário
- **Método**: POST
- **URL**: `/api/usuarios`
- **Corpo**:
  ```json
  {
    "nome": "Novo Usuário",
    "email": "novo@email.com",
    "senha": "senha123"
  }
  ```
- **Respostas**:
  - `201 Created`: Usuário criado com sucesso.
  - `400 Bad Request`: Dados obrigatórios ausentes.
  - `409 Conflict`: E-mail já registrado.

### 2. Listar Todos os Usuários
- **Método**: GET
- **URL**: `/api/usuarios`
- **Resposta**:
  ```json
  [
    {
      "id": 1,
      "nome": "Usuario1",
      "email": "usuario1@email.com"
    },
    {
      "id": 2,
      "nome": "Usuario2",
      "email": "usuario2@email.com"
    }
  ]
  ```

### 3. Buscar Usuário por ID
- **Método**: GET
- **URL**: `/api/usuarios/{id}`
- **Respostas**:
  - `200 OK`: Detalhes do usuário.
  - `404 Not Found`: Usuário não encontrado.

### 4. Atualizar Usuário
- **Método**: PUT
- **URL**: `/api/usuarios/{id}`
- **Corpo**:
  ```json
  {
    "nome": "Usuário Atualizado",
    "email": "atualizado@email.com",
    "senha": "novaSenha123"
  }
  ```
- **Respostas**:
  - `200 OK`: Usuário atualizado com sucesso.
  - `400 Bad Request`: Dados obrigatórios ausentes.
  - `404 Not Found`: Usuário não encontrado.
  - `409 Conflict`: E-mail já registrado.

### 5. Deletar Usuário
- **Método**: DELETE
- **URL**: `/api/usuarios/{id}`
- **Respostas**:
  - `204 No Content`: Usuário deletado com sucesso.
  - `404 Not Found`: Usuário não encontrado.

---

## Contribuindo

1. Fork o repositório.
2. Crie uma branch:
   ```bash
   git checkout -b feature/nome-da-feature
   ```
3. Faça suas alterações.
4. Teste suas alterações.
5. Envie suas alterações:
   ```bash
   git push origin feature/nome-da-feature
   ```
6. Crie um Pull Request.

---

## Licença

Este projeto está licenciado sob a Licença MIT. Consulte o arquivo `LICENSE` para mais detalhes.
