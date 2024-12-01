package com.suporte.microservico.microservico_springboot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Anotação que indica que a classe é uma configuração do Spring
public class SwaggerConfig {

    // Cria um bean que configura o Swagger para a documentação da API
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Define o nome do esquema de segurança (bearerAuth)

        return new OpenAPI() // Configura o objeto OpenAPI para gerar a documentação
                .info(new Info() // Informações gerais sobre a API
                        .title("API com JWT") // Título da API
                        .version("1.0") // Versão da API
                        .description("Teste de autenticação JWT via Swagger")) // Descrição da API
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Define que a API vai usar autenticação com JWT
                .components(new Components() // Configura os componentes da API, incluindo o esquema de segurança
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme() // Adiciona o esquema de segurança para JWT
                                .name(securitySchemeName) // Nome do esquema de segurança
                                .type(SecurityScheme.Type.HTTP) // Tipo de autenticação HTTP
                                .scheme("bearer") // Tipo de esquema, que é o "bearer" para tokens JWT
                                .bearerFormat("JWT"))); // Define o formato do token como JWT
    }
}
