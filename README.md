# Auto Repair Shop API

Back-end monolítico para apoiar a gestão de uma oficina mecânica, com foco
em clientes, veículos, peças e ordens de serviço.

## Arquitetura

Aplicação monolítica desenvolvida com Spring Boot e organizada em camadas.
A estrutura detalhada será documentada conforme o projeto evoluir.

## Tecnologias

- Java 25
- Spring Boot 4.1.1
- Maven

## Pré-requisitos

- Java 25

## Execução local

macOS/Linux:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Testes

macOS/Linux:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

## Banco de dados

A decisão e a justificativa do banco de dados serão incluídas após a escolha
da tecnologia.

## Documentação da API

A documentação Swagger/OpenAPI será incluída quando estiver configurada.

## Documentação DDD

A documentação do Event Storming, dos diagramas e da linguagem ubíqua ficará
em `docs/ddd/`.

## Status do projeto

Estrutura inicial da aplicação. As funcionalidades do MVP ainda serão
implementadas.