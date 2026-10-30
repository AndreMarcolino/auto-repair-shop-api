# Auto Repair Shop API

Back-end monolítico para gestão de oficina mecânica, desenvolvido como MVP do Tech Challenge da Fase 1 do curso **SOAT — Software Architecture**.

## Objetivo e solução

Centralizar clientes, veículos, serviços, peças/insumos, estoque e ordens de serviço (OS), substituindo controles manuais por uma API REST.

A implementação identificada contempla cadastro administrativo, abertura e acompanhamento de OS, elaboração e decisão de orçamento, movimentação de estoque, pagamento, entrega, consulta pelo cliente e cálculo de tempos médios. Peças são representadas pelo modelo de insumos.

**Status:** essas funcionalidades foram identificadas por análise estática; a validação funcional completa da versão atual ainda está pendente. Consulte a seção “Status da validação”.

## Arquitetura

Monólito em camadas, com características de portas e adaptadores, organizado no pacote `br.com.autorepairshop`.

| Camada | Responsabilidade |
|---|---|
| `controller` | Endpoints REST, DTOs e validação HTTP |
| `application` | Casos de uso, coordenação de operações e transações |
| `domain` | Entidades, agregados, objetos de valor, regras e contratos de repositório |
| `infrastructure` | Persistência JPA, adaptadores, segurança, eventos e OpenAPI |

Fluxo: **Controller → caso de uso → domínio/repositórios → adaptadores → PostgreSQL**.

Os modelos de domínio são separados das entidades JPA. A aplicação utiliza recursos Spring e algumas dependências diretas da infraestrutura; portanto, a separação de portas e adaptadores não é estrita.

Os serviços utilizam `@Transactional`. Os eventos internos `EntradaEstoqueRegistrada` e `OrdemServicoFinalizada` são tratados após commit, sem comprovação de mensageria externa ou entrega durável. A persistência desses fluxos e o comportamento concorrente ainda precisam de testes.

## Tecnologias

Java 25, Spring Boot 4.1.1, Maven Wrapper, Spring Web, Spring Data JPA, PostgreSQL 17, Flyway, Bean Validation, Spring Security, OAuth2 Resource Server/JOSE, Springdoc OpenAPI 3.0.0, Docker e Docker Compose.

Para testes, o projeto declara Spring Boot Test e Testcontainers para PostgreSQL; o uso efetivo de Testcontainers ainda precisa ser confirmado.

## Pré-requisitos

| Modalidade | Requisitos |
|---|---|
| Ambiente completo pelo Docker | Docker em execução, Docker Compose e portas 8080/5432 disponíveis |
| API pelo Maven | JDK 25, Maven Wrapper incluído, PostgreSQL pelo Docker e portas 8080/5432 disponíveis |

Java e Maven locais não são necessários para executar o ambiente completo pelo Docker.

## Configuração

Execute os comandos na raiz do projeto. Crie `.env` a partir de `.env.example`, após confirmar que esse arquivo está disponível.

Windows — PowerShell:

    Copy-Item .env.example .env

Linux/macOS:

    cp .env.example .env

Configure `DB_PASSWORD` conforme o banco local. A aplicação também permite `DB_URL` e `DB_USERNAME`.

**JWT:** confirme como o segredo é lido pela configuração e repassado pelo Compose. `JWT_SECRET` só terá efeito se a aplicação utilizar essa variável. Para HS256 com decodificação Base64, são necessários pelo menos 32 bytes após a decodificação.

Não versione `.env`, segredos, tokens ou credenciais reais. Alterar `DB_PASSWORD` não modifica a senha de um banco já persistido no volume.

### Perfil local

O perfil padrão é `local`, que configura o Flyway com `db/migration` e `db/seed`.

`R__dados_fake.sql` fornece dados de demonstração. É uma migração repetível, reexecutada quando seu checksum muda; os inserts com `ON CONFLICT DO NOTHING` não restauram registros alterados.

Fora da demonstração local, configure explicitamente o perfil e confira as localizações efetivas do Flyway. As contas de funcionários da V17 pertencem às migrações versionadas, não apenas ao seed local.

## Execução com Docker Compose

Validar, construir e iniciar:

    docker compose config --quiet
    docker compose up -d --build
    docker compose ps

Acompanhar os logs:

    docker compose logs -f api
    docker compose logs -f postgres

O Flyway aplica as migrações pendentes. A mensagem `Started AutoRepairShopApiApplication` indica inicialização, não validação dos fluxos.

Encerrar preservando o banco:

    docker compose down

**Atenção:** `docker compose down -v` remove volumes e pode apagar os dados. Use `Ctrl+C` para sair dos logs sem encerrar os serviços.

## Execução da API pelo Maven

Não mantenha a API do Compose ocupando a porta 8080. Inicie somente o banco:

    docker compose up -d postgres
    docker compose ps

O Maven não carrega automaticamente o `.env`. Defina as variáveis necessárias no terminal.

Windows — PowerShell:

    $env:DB_PASSWORD = "SUBSTITUA_PELA_SENHA_LOCAL"
    $env:JWT_SECRET = "SUBSTITUA_PELO_SEGREDO_COMPATIVEL"
    .\mvnw.cmd spring-boot:run

Linux/macOS:

    export DB_PASSWORD='SUBSTITUA_PELA_SENHA_LOCAL'
    export JWT_SECRET='SUBSTITUA_PELO_SEGREDO_COMPATIVEL'
    chmod +x mvnw
    ./mvnw spring-boot:run

Confirme a integração de `JWT_SECRET` com a configuração antes de utilizá-la. Mantenha o terminal aberto e use `Ctrl+C` para encerrar.

## Documentação da API

Com a aplicação em execução:

- <a href="http://localhost:8080/swagger-ui.html" target="_blank" style="text-decoration: underline;">Swagger UI</a>
- <a href="http://localhost:8080/v3/api-docs" target="_blank" style="text-decoration: underline;">OpenAPI</a>

O esquema Bearer é denominado `bearerAuth`. As anotações OpenAPI documentam a segurança; a proteção efetiva depende do Spring Security e dos casos de uso.

### Principais recursos

| Recurso | Caminho |
|---|---|
| Clientes e consulta por documento | `/api/clientes` e `/api/clientes/por-cpf-cnpj` |
| Veículos, serviços e insumos | `/api/veiculos`, `/api/servicos`, `/api/insumos` |
| Entrada de estoque | `/api/estoque/entrada` |
| Abertura, listagem e detalhamento de OS | `/api/ordens-servico` |
| Orçamento | `/api/ordens-servico/{id}/orcamento` |
| Consulta do cliente | `/api/ordens-servico/cliente/detalhamento` |
| Aprovação/reprovação | `/api/ordens-servico/aprovar` e `/api/ordens-servico/reprovar` |
| Tempos médios | `/api/ordens-servico/tempo-medio-execucao` |

Consulte métodos, DTOs e parâmetros completos no Swagger.

A abertura retorna número público no formato `OS-AAAA-NNNNNN` e código de acesso; seu hash é persistido. O UUID permanece nas operações administrativas.

A consulta pública utiliza `numeroOrdemServico`, `codigoVerificacao` e `cpfCnpj`. Aprovação e reprovação recebem esses campos em JSON.

As médias consideram ordens entregues: atendimento corresponde a abertura–entrega e execução a início–fim do serviço. Datas insuficientes são excluídas da média correspondente; sem amostras, o resultado é zero. O indicador é calculado por OS, não por item de serviço.

## Banco de dados

**PostgreSQL 17**, escolhido pela integridade referencial, transações, restrições de consistência e consultas relacionais adequadas ao domínio, além da integração com JPA e Flyway.

- Banco: `auto_repair_shop`; serviço Compose: `postgres`; volume: `postgres_data`.
- Maven: `jdbc:postgresql://localhost:5432/auto_repair_shop`.
- Compose: `jdbc:postgresql://postgres:5432/auto_repair_shop`.
- Migrações: `src/main/resources/db/migration/`.
- Hibernate: `spring.jpa.hibernate.ddl-auto=none`.

A revisão identificou migrações até V27, que adiciona número público, hash do código e sequência. A validação local anterior confirmou V26; V27 e seed ainda precisam ser executados na versão atual.

A V27 não preenche os novos campos de ordens anteriores. Seu tratamento deve ser definido antes de utilizar essas ordens nos novos fluxos.

## Autenticação

Funcionários ativos autenticam por e-mail e senha BCrypt. A aplicação emite JWT HS256, com identidade em `sub` e papel em `tipo`, convertido para autoridade `ROLE_`.

### Contas de demonstração

| E-mail | Papel |
|---|---|
| `atendente@auto-repair-shop.com.br` | ATENDENTE |
| `mecanico@auto-repair-shop.com.br` | MECANICO |

Inseridas pela V17. A senha indicada no código auxiliar é `123456`; sua correspondência com os hashes ainda precisa ser validada. Essas contas são exclusivas de demonstração, nunca de produção.

### Login e uso do token

Na versão revisada, o login utiliza `GET /api/autenticacao/login` com corpo JSON:

    {
      "email": "atendente@auto-repair-shop.com.br",
      "senha": "123456"
    }

Envie pelo Postman: método GET, corpo raw/JSON e cabeçalho `Content-Type: application/json`. GET com corpo pode não funcionar no Swagger ou em clientes de navegador; a alteração para POST ainda está pendente.

A resposta contém `accessToken`, `tokenType` e `expiresIn`. No Swagger, selecione `Authorize` e cole somente o token em `bearerAuth`.

Cabeçalho esperado:

    Authorization: Bearer <token>

### Permissões

- **ATENDENTE:** clientes, veículos, escrita de serviços/insumos, entrada de estoque, abertura, pagamento, entrega e tempos médios.
- **MECANICO:** diagnóstico, início e finalização de serviço.
- **Ambos:** leitura de serviços/insumos e listagem de OS.
- **Autenticação global sem papel explícito:** detalhamento administrativo e operações de orçamento.
- **Sem JWT:** login, consulta do cliente, aprovação/reprovação e geradores auxiliares de token.

A identidade nas inclusões de orçamento passou a ser obtida do JWT. As permissões e verificações adicionais precisam de validação funcional.

## Testes e cobertura

Os testes unitários ficam em `src/test/java` e utilizam JUnit 5 e Mockito para testar casos de uso de forma isolada. Os testes unitários descritos nesta seção não exigem a inicialização do contexto completo do Spring, PostgreSQL ou Docker.

### Executar a suíte de testes

Windows — PowerShell:

    .\mvnw.cmd test

Linux/macOS:

    ./mvnw test

### Executar uma classe específica

Windows — PowerShell:

    .\mvnw.cmd "-Dtest=AdicionarInsumoServiceTest" test
    .\mvnw.cmd "-Dtest=DetalharOrdemServicoClienteServiceTest" test

Linux/macOS:

    ./mvnw "-Dtest=AdicionarInsumoServiceTest" test
    ./mvnw "-Dtest=DetalharOrdemServicoClienteServiceTest" test

### Cenários cobertos pelos testes ajustados

`AdicionarInsumoServiceTest` cobre:

- adição de insumo ao orçamento e salvamento do orçamento;
- rejeição de ordem de serviço nula;
- rejeição de lista de insumos vazia;
- rejeição de usuário nulo;
- rejeição de UUID inválido para OS ou insumo;
- rejeição quando o insumo não é encontrado;
- rejeição quando o orçamento não é encontrado.

`DetalharOrdemServicoClienteServiceTest` cobre:

- rejeição de documento nulo, em branco ou inválido;
- rejeição de código de verificação nulo ou em branco;
- rejeição quando o cliente não é encontrado;
- retorno nulo quando a OS não é encontrada;
- rejeição quando a OS pertence a outro cliente;
- rejeição de código de acesso incorreto;
- detalhamento da OS sem orçamento;
- detalhamento da OS com orçamento sem itens.

**Pendente:** confirmar que as classes atualizadas foram salvas e executar os comandos acima na versão final. A descrição dos cenários não comprova que os testes foram executados com sucesso.

O projeto declara Testcontainers para PostgreSQL; confirme quais testes o utilizam e seus requisitos. Caso existam testes de integração que iniciem contêineres, o Docker deverá estar disponível para executá-los.

**Cobertura:** não há evidência apresentada de cobertura mínima de 80%. Para verificar esse requisito, configure e execute uma ferramenta de cobertura, como JaCoCo, e registre a métrica adotada, o resultado e os domínios críticos avaliados. Domínios críticos propostos: transições de OS, orçamento, estoque, CPF/CNPJ e placa, autenticação/autorização, código de acesso e coordenação transacional.

O build Docker utiliza `-DskipTests`; construir a imagem não comprova aprovação dos testes.

## Estrutura do projeto

    .
    ├── .mvn/                         # Maven Wrapper
    ├── src/main/java/br/com/autorepairshop/
    │   ├── controller/               # HTTP e DTOs
    │   ├── application/              # Casos de uso
    │   ├── domain/                   # Modelos e regras
    │   └── infrastructure/           # JPA, segurança, eventos e config
    ├── src/main/resources/
    │   ├── application.properties
    │   ├── application-local.properties
    │   └── db/
    │       ├── migration/            # Migrações Flyway
    │       └── seed/                 # Dados de demonstração
    ├── src/test/                     # Testes
    ├── zap-report/zap-report.html    # Relatório de scan
    ├── Dockerfile
    ├── docker-compose.yml
    ├── pom.xml
    ├── mvnw
    ├── mvnw.cmd
    └── README.md

## Documentação DDD

<a href="https://miro.com/app/board/uXjVHqZ6t4s=/" target="_blank" style="text-decoration: underline;">Documentação DDD no Miro</a>

A modelagem deve representar a versão entregue e incluir Event Storming de criação/acompanhamento da OS e gestão de peças/insumos, atores, comandos, eventos, políticas, agregados, entidades, objetos de valor, diagramas, linguagem ubíqua e glossário. Bounded Contexts devem ser apresentados quando identificados.

No código, foram identificados modelos como `OrdemServico`, `Orcamento`, `Cliente`, `Veiculo`, `Estoque` e `Funcionario`, além de objetos de valor para CPF/CNPJ, e-mail e placa.

**Pendente:** confirmar acesso sem solicitação de permissão e exportações no repositório, preferencialmente em `docs/ddd/`. Eventos conceituais da modelagem devem ser distinguidos dos eventos realmente publicados pela aplicação.

## Análise de vulnerabilidades

Relatório versionado: `zap-report/zap-report.html`, incluído em `a86ac6b`.

**Pendente:** revisar conteúdo e registrar ferramenta/versão, data, procedimento, alvo, autenticação, commit analisado, alertas e tratamento. A existência do arquivo não comprova ausência de vulnerabilidades nem cobertura de todas as rotas.

## Status da validação e limitações

Referência da revisão estática: branch `develop`, commit `a86ac6bfed741c8a66d97516234bf674c9809191`.

| Situação | Evidência |
|---|---|
| Validado em versão anterior | API por Maven/Docker, PostgreSQL, conexão Compose, Flyway até V26, HTTP 8080 e Swagger |
| Identificado no código atual | CRUDs, estoque, OS, orçamento, consulta do cliente, médias, JWT, V27 e seed |
| Correções identificadas, sem regressão executada | Uso de `@PathVariable`, preenchimento de `dataEntrega` e identidade JWT no orçamento |
| Ainda não comprovado | Fluxos completos da versão atual, autorização, V27/seed, transações, concorrência, execução dos testes e cobertura |

As principais pendências técnicas registradas na revisão são:

- Login GET com corpo e geradores públicos de token.
- Comparação do código de acesso na reprovação não evidenciada.
- Tratamento incorreto de CNPJ e validação do número da OS nos fluxos do cliente.
- Construção direta de objetos de valor, contornando as fábricas de validação.
- Possível acesso a estoque nulo na aprovação e necessidade de proteger quantidades no caso de uso.
- Persistência após commit, consistência de datas do seed e durações negativas.
- Permissões, expiração/validação JWT, revogação e proteção contra tentativas repetidas.
- Tratamento de ordens anteriores à V27, paginação e critérios de ordenação.

Esses apontamentos devem ser reavaliados na versão final e acompanhados de testes. Atualizar o README não aplica correções ao código.

## Vídeo da entrega

Link público no YouTube: **pendente**.

Duração máxima: **15 minutos**. O vídeo deve corresponder ao commit entregue, identificar os integrantes e demonstrar proposta, arquitetura, banco, DDD/Event Storming, execução, Swagger, fluxos da OS/orçamento, autenticação, testes e resultado da análise de vulnerabilidades.

## Versão final e submissão

- Branch da entrega: **pendente**.
- Hash completo do commit: **pendente**.

Informe branch e hash também no PDF de submissão. Código, documentação, relatórios e vídeo devem corresponder à mesma versão.

Mantenha o repositório público durante a correção/revisão de notas, sem segredos ou informações sensíveis. A branch `main` deve permanecer protegida e receber alterações por Pull Requests.