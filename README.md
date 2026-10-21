# Auto Repair Shop API

Back-end para gestão de uma oficina mecânica, desenvolvido como MVP do Tech Challenge da Fase 1 do curso SOAT — Software Architecture.

## Objetivo

Centralizar a gestão de clientes, veículos, serviços, peças, insumos e ordens de serviço, reduzindo a dependência de controles manuais e facilitando o acompanhamento dos atendimentos.

## Descrição da solução

A solução disponibiliza uma API REST para apoiar os processos administrativos e operacionais da oficina.

O escopo previsto contempla:

- Cadastro e gestão de clientes e veículos.
- Cadastro de serviços, peças e insumos.
- Controle de estoque.
- Criação e acompanhamento de ordens de serviço.
- Geração de orçamento com base nos serviços e materiais utilizados.
- Aprovação ou rejeição do orçamento.
- Controle das transições de status da ordem de serviço.
- Consulta do andamento da ordem de serviço pelo cliente.
- Cálculo do tempo médio de execução dos serviços.
- Autenticação JWT para as APIs administrativas.

A implementação e a validação de cada fluxo devem ser conferidas no código e nos testes. A inicialização da aplicação, por si só, não comprova o funcionamento de todas as funcionalidades.

## Arquitetura

A aplicação utiliza uma arquitetura monolítica com Spring Boot.

A organização em camadas e os elementos de Domain-Driven Design (DDD) deverão ser detalhados conforme a estrutura efetivamente implementada no código.

Componentes técnicos confirmados:

- Spring Web para exposição da API HTTP.
- Spring Data JPA para persistência.
- PostgreSQL como banco de dados.
- Flyway para controle das migrações.
- Spring Security e bibliotecas OAuth2 Resource Server/JOSE para suporte à segurança.
- Springdoc para documentação OpenAPI e Swagger UI.

O ambiente Docker possui dois serviços:

- `api`: aplicação Java, construída pelo `Dockerfile`.
- `postgres`: banco de dados PostgreSQL.

A API acessa o banco pela rede interna do Compose, utilizando o endereço `postgres:5432`.

## Tecnologias

| Tecnologia | Utilização |
|---|---|
| Java 25 | Linguagem e ambiente de execução |
| Spring Boot 4.1.1 | Framework da aplicação |
| Maven e Maven Wrapper | Gerenciamento de dependências e build |
| Spring Web | API REST |
| Spring Data JPA | Persistência |
| PostgreSQL 17 | Banco de dados |
| Flyway | Migrações do banco |
| Bean Validation | Infraestrutura de validação |
| Spring Security | Infraestrutura de segurança |
| OAuth2 Resource Server e JOSE | Suporte a JWT |
| Springdoc OpenAPI 3.0.0 | Documentação da API |
| Docker e Docker Compose | Build e execução do ambiente |
| Spring Boot Test | Infraestrutura de testes |

O projeto também declara dependências de Testcontainers para PostgreSQL em escopo de teste. Seu uso efetivo nos testes ainda deve ser verificado.

## Pré-requisitos

### Execução do ambiente completo pelo Docker

- Docker instalado e em execução.
- Docker Compose disponível.
- Git, caso seja necessário clonar o repositório.
- Portas `8080` e `5432` disponíveis.

Nesta modalidade, não é necessário instalar Java ou Maven na máquina.

### Execução da API pelo Maven

- JDK 25.
- Docker com Compose para executar o PostgreSQL.
- Portas `8080` e `5432` disponíveis.

O Maven Wrapper está incluído no projeto, dispensando a instalação separada do Maven.

## Configuração do ambiente

Na raiz do projeto, crie um arquivo `.env` a partir de `.env.example`.

### Windows — PowerShell

    Copy-Item .env.example .env

### Linux ou macOS

    cp .env.example .env

Preencha as variáveis:

| Variável | Descrição |
|---|---|
| `DB_PASSWORD` | Senha do PostgreSQL |
| `JWT_SECRET` | Segredo utilizado pela configuração JWT |

O formato e o tamanho de `JWT_SECRET` devem ser compatíveis com a implementação de segurança da aplicação.

**Não versione o arquivo `.env` nem publique segredos, tokens ou credenciais reais.**

O `.env.example` deve conter somente valores ilustrativos ou marcadores de preenchimento.

> Se o banco já tiver sido criado, mantenha a senha utilizada na sua inicialização. Alterar `DB_PASSWORD` não altera automaticamente a senha de um banco existente no volume.

## Execução com Docker Compose

Execute os comandos na raiz do projeto.

### 1. Validar a configuração

    docker compose config --quiet

Se o comando terminar sem mensagens de erro, a configuração foi validada.

### 2. Construir e iniciar o ambiente

    docker compose up -d --build

O Compose inicia o PostgreSQL, aguarda o banco ficar saudável e inicia a API.

Durante a inicialização, o Flyway valida as migrações existentes e aplica as pendentes.

### 3. Verificar os serviços

    docker compose ps

### 4. Acompanhar os logs

API:

    docker compose logs -f api

Banco de dados:

    docker compose logs -f postgres

A mensagem `Started AutoRepairShopApiApplication` indica que a inicialização da aplicação foi concluída.

Pressione `Ctrl+C` para sair do acompanhamento dos logs sem encerrar os contêineres.

### 5. Encerrar o ambiente

    docker compose down

Esse comando preserva o volume do PostgreSQL.

**Atenção:** o comando abaixo também remove o volume e apaga os dados do banco:

    docker compose down -v

## Execução da API pelo Maven

Nesta modalidade, somente o PostgreSQL é executado pelo Docker.

Não mantenha a API do Compose rodando na porta `8080` ao iniciar a aplicação pelo Maven.

### 1. Iniciar o banco

    docker compose up -d postgres

Verifique se o banco está saudável:

    docker compose ps

Como o arquivo Compose exige variáveis de ambiente, o `.env` deve estar preenchido mesmo ao iniciar somente o banco.

### 2. Configurar o segredo JWT no terminal

O Docker Compose lê o `.env` automaticamente, mas a execução pelo Maven não o carrega automaticamente.

Windows — PowerShell:

    $env:JWT_SECRET = "SUBSTITUA_PELO_SEGREDO_LOCAL"

Linux ou macOS:

    export JWT_SECRET='SUBSTITUA_PELO_SEGREDO_LOCAL'

Caso a senha do banco seja diferente do padrão de desenvolvimento configurado na aplicação, defina também `DB_PASSWORD` no ambiente do terminal.

### 3. Iniciar a aplicação

Windows — PowerShell:

    .\mvnw.cmd spring-boot:run

Linux ou macOS:

    chmod +x mvnw
    ./mvnw spring-boot:run

O Maven Wrapper baixa as dependências necessárias, compila o código e inicia a aplicação.

Mantenha o terminal aberto enquanto utilizar a API. Para encerrá-la, pressione `Ctrl+C`.

## Documentação da API

Com a aplicação em execução, acesse:

- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [Especificação OpenAPI](http://localhost:8080/v3/api-docs)

A abertura do Swagger foi validada no ambiente local executado pelo Docker.

As exigências de autenticação de cada endpoint devem ser verificadas na configuração de segurança e na documentação da API.

## Banco de dados

A aplicação utiliza PostgreSQL 17.

### Configuração

- Banco: `auto_repair_shop`.
- Serviço no Compose: `postgres`.
- Porta publicada na máquina: `5432`.
- Persistência dos dados: volume `postgres_data`.

Na execução local pelo Maven, a conexão padrão aponta para:

    jdbc:postgresql://localhost:5432/auto_repair_shop

Dentro do Compose, a API utiliza:

    jdbc:postgresql://postgres:5432/auto_repair_shop

A configuração da aplicação permite utilizar:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

### Migrações

As migrações ficam em:

    src/main/resources/db/migration/

O gerenciamento do esquema é realizado pelo Flyway. A geração automática de tabelas pelo Hibernate está desabilitada com `spring.jpa.hibernate.ddl-auto=none`.

Na validação local realizada, o Flyway confirmou 26 migrações e o esquema na versão `v26`.

### Justificativa da escolha

O PostgreSQL oferece recursos adequados a um domínio com relacionamentos entre clientes, veículos, ordens de serviço, serviços e materiais:

- Integridade referencial.
- Transações para operações relacionadas.
- Restrições de consistência.
- Consultas relacionais e agregações.
- Integração com Spring Data JPA e Flyway.

**Pendente:** confirmar essa justificativa com o grupo e registrar eventuais critérios ou alternativas considerados na decisão.

## Autenticação e segurança

O projeto possui dependências e configuração de suporte a JWT.

O segredo deve ser fornecido por variável de ambiente, sem permanecer fixo no código ou ser publicado no repositório.

Antes da entrega, esta seção deverá ser complementada com:

- Endpoint de autenticação e exemplo de requisição.
- Procedimento para criar ou obter um usuário de demonstração.
- Exemplo de resposta com o token.
- Instruções para utilizar `Authorization: Bearer <token>`.
- Identificação das rotas administrativas protegidas.
- Forma de acesso do cliente à consulta da ordem de serviço.

Não utilize credenciais reais como credenciais de demonstração.

## Testes

### Executar os testes

Windows — PowerShell:

    .\mvnw.cmd test

Linux ou macOS:

    ./mvnw test

Os requisitos de infraestrutura dos testes devem ser confirmados conforme a implementação. Caso utilizem Testcontainers, o Docker deverá estar em execução.

### Cobertura

A atividade exige cobertura mínima de 80% nos domínios críticos, além de testes unitários e de integração dos principais fluxos.

**Pendências:**

- Confirmar os testes implementados.
- Identificar os domínios críticos.
- Configurar ou confirmar a ferramenta de cobertura.
- Documentar o comando de geração do relatório.
- Disponibilizar a evidência da cobertura obtida.

O build da imagem Docker utiliza `-DskipTests`. Portanto, construir a imagem com sucesso não significa que os testes foram executados ou aprovados.

## Estrutura dos principais diretórios

Estrutura conhecida e arquivos de infraestrutura:

    .
    ├── .mvn/                   # Configuração do Maven Wrapper
    ├── src/
    │   ├── main/               # Código e recursos da aplicação
    │   │   └── resources/
    │   │       └── db/
    │   │           └── migration/
    │   └── test/               # Código de testes
    ├── Dockerfile              # Build e execução da API
    ├── docker-compose.yml      # API e PostgreSQL
    ├── pom.xml                 # Dependências e build
    ├── mvnw                    # Maven Wrapper para Linux/macOS
    ├── mvnw.cmd                # Maven Wrapper para Windows
    └── README.md

Os arquivos `.env.example`, `.dockerignore` e as regras de exclusão do `.env` devem ser conferidos antes da entrega.

A estrutura detalhada dos pacotes e das camadas ainda deverá ser documentada com base no código.

## Documentação DDD

A documentação deverá representar a implementação entregue e incluir:

- Event Storming dos fluxos principais.
- Eventos de domínio, comandos, atores e políticas.
- Agregados, entidades e objetos de valor.
- Bounded Contexts, quando identificados.
- Diagramas da solução.
- Linguagem ubíqua e glossário.
- Relação entre os elementos de DDD e o código.

Estrutura prevista:

    docs/
    └── ddd/
        ├── event-storming.pdf
        ├── diagramas.pdf
        └── linguagem-ubiqua.md

**Link da documentação DDD:** pendente.

A documentação externa deverá permitir acesso sem solicitação manual de permissão. As exportações também deverão ser armazenadas no repositório.

## Análise de vulnerabilidades

**Pendente:** executar a análise de vulnerabilidades e disponibilizar o relatório ou resultado no repositório.

Documentar a ferramenta utilizada, o comando executado, os resultados e o tratamento dos problemas identificados.

## Vídeo da entrega

**Link do vídeo público no YouTube:** pendente.

O vídeo deverá corresponder à versão final entregue, ter duração máxima de 15 minutos e demonstrar a solução, a arquitetura, os fluxos principais, a segurança, os testes e a análise de vulnerabilidades.

## Status da validação

### Validado localmente

- Inicialização do PostgreSQL pelo Docker Compose.
- Execução da API pelo Maven Wrapper.
- Build da imagem da aplicação pelo Dockerfile.
- Execução conjunta da API e do banco pelo Docker Compose.
- Conexão da API com o PostgreSQL pela rede do Compose.
- Validação das 26 migrações pelo Flyway.
- Inicialização HTTP na porta `8080`.
- Abertura do Swagger UI.

### Ainda a verificar ou documentar

- Funcionalidades e regras de negócio do MVP.
- Transições de status das ordens de serviço.
- Autenticação e autorização.
- Validação de CPF/CNPJ e placa.
- Testes unitários e de integração.
- Cobertura mínima de 80% nos domínios críticos.
- Tratamento de transações dos fluxos principais.
- Estrutura das camadas e documentação DDD.
- Relatório de vulnerabilidades.
- Vídeo público da entrega.

## Versão da entrega

Preencher antes da submissão:

- Branch da entrega: pendente.
- Hash completo do commit: pendente.

O código, a documentação e o vídeo devem corresponder à mesma versão.

A branch `main` deve permanecer protegida, recebendo alterações por Pull Requests.