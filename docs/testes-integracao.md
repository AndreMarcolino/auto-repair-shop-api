# Testes de integração dos fluxos principais

Evidência de testes automatizados do Tech Challenge (Fase 1): **integração dos fluxos principais** e
**cobertura mínima de 80% nos domínios críticos**.

## O que é testado

Os testes sobem a aplicação completa (controllers, casos de uso, domínio, JPA, Flyway e segurança JWT) em porta
aleatória, conectada a um **PostgreSQL real** em container (Testcontainers), e chamam a API por HTTP.

| Classe | O que cobre |
|---|---|
| `AutenticacaoIntegrationTest` | Login, 401 sem/ com token inválido, 403 por papel (atendente × mecânico) |
| `CadastrosIntegrationTest` | Clientes (PF/PJ, CPF inválido/duplicado, desativação), veículos (placa, ano), serviços e insumos |
| `FluxoOrdemServicoIntegrationTest` | Fluxo completo RECEBIDA → ENTREGUE (orçamento R$ 300,00), reprovação, aguardar peças + liberação por entrada de estoque, regras de bloqueio |
| `EstoqueIntegrationTest` | Entrada de estoque, validação, permissão |
| `DefeitosConhecidosIntegrationTest` | Testes `@Disabled` que descrevem o comportamento correto de defeitos encontrados |

## Como executar

Pré-requisitos: **Java 25** e **Docker em execução** (o banco de teste sobe sozinho).

```bash
./mvnw test      # roda os testes e gera os relatórios de cobertura
./mvnw verify    # idem, e FALHA se a cobertura de linhas do domínio ficar abaixo de 80%
```

## Evidência de cobertura (JaCoCo)

Após `./mvnw test` abra no navegador:

- `target/site/jacoco-dominio/index.html` — **cobertura do pacote `domain` (domínio crítico)**
- `target/site/jacoco/index.html` — cobertura do projeto inteiro

Print dessas páginas, junto da saída de `./mvnw verify` com `BUILD SUCCESS`, é a evidência dos 80%.
A regra está no `pom.xml` (execução `verificar-cobertura-dominio`: contador LINE, mínimo 0.80).

## Dados usados

Cada teste cria seus próprios dados pela API (CPF/CNPJ/placa gerados e únicos), então a ordem de execução não
importa. O profile `test` usa apenas as migrations; os funcionários de demonstração vêm da V17
(`atendente@auto-repair-shop.com.br` e `mecanico@auto-repair-shop.com.br`, senha `123456`).

## Defeitos encontrados

| Defeito | Onde corrigir |
|---|---|
| Login com senha errada responde 500 em vez de 401 | Mapear a exceção de credenciais inválidas em `TratamentoExcecoesApi` para 401 |
| Aprovar/reprovar/consultar OS por **CNPJ** falha: o cliente é buscado como pessoa física | Serviços de aprovar, reprovar e detalhamento do cliente: derivar o tipo de pessoa do documento informado |
| Login é `GET` com corpo JSON | Trocar para `POST` (GET com corpo não é suportado por vários clientes/proxies) |
| Endpoints `gera-token-*` públicos | Remover ou proteger (permitAll no `SecurityConfig`) |
| `.env` e segredo JWT versionados | Remover do repositório, rotacionar o segredo e usar variáveis de ambiente |
