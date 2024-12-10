package com.suporte.microservico.microservico_springboot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração do Swagger para documentar e testar a API com autenticação JWT.
 */
@Configuration // Indica que esta classe contém configurações Spring.
public class SwaggerConfig {

    /**
     * Define as configurações do Swagger para a API.
     *
     * @return uma instância personalizada de OpenAPI.
     */
    @Bean // Declaração de um bean gerenciado pelo Spring.
    public OpenAPI customOpenAPI() {
        // Nome do esquema de segurança utilizado para autenticação.
        final String securitySchemeName = "bearerAuth";

        // Configurações principais da documentação OpenAPI.
        return new OpenAPI()
                .info(new Info()
                        .title("API com JWT") // Título da documentação.
                        .version("1.0") // Versão da API.
                        .description("Teste de autenticação JWT via Swagger")) // Descrição da API.
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Requisito de segurança para autenticação.
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName) // Nome do esquema de segurança.
                                .type(SecurityScheme.Type.HTTP) // Tipo de segurança (HTTP).
                                .scheme("bearer") // Esquema de autenticação Bearer Token.
                                .bearerFormat("JWT"))); // Formato do token utilizado.
    }
}
