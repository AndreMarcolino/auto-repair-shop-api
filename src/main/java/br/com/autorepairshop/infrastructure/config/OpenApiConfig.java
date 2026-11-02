
package br.com.autorepairshop.infrastructure.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(
	    name = "bearerAuth",
	    type = SecuritySchemeType.HTTP,
	    scheme = "bearer",
	    bearerFormat = "JWT"
	)
@OpenAPIDefinition(
    info = @Info(
        title = "Auto Repair Shop API",
        version = "1.0.0",
        description = """
            ## Tech Challenge — FIAP

            **Pós-Tech:** Software Architecture

            **Fase:** 1 — MVP

            **Projeto:** Sistema de gerenciamento de ordens de serviço
            para oficinas mecânicas.

            ### Funcionalidades
            - Gerenciamento de clientes, veículos, insumos e serviços.
            - Abertura e acompanhamento de ordens de serviço.
            - Diagnóstico e elaboração de orçamentos.
            - Aprovação de orçamentos.
            - Controle de execução dos serviços.

            ### Tecnologias
            - Java
            - Spring Boot
            - Spring Security com JWT
            - PostgreSQL
            - Flyway
            - Docker
            """,
        contact = @Contact(
            name = "Equipe do projeto"
        ),
        license = @License(
            name = "Projeto acadêmico — FIAP"
        )
    )
)
public class OpenApiConfig {
}
