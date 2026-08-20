# Monitoolring — Backend

Backend do projeto Monitoolring: Java 21, Spring Boot 3, Maven.

> A branch `master` é protegida e não recebe commits diretos. Todo o desenvolvimento parte da branch `inicializacao-projeto` (ou de branches derivadas dela), integrado via Pull Request.

## Stack

- Java 21
- [Spring Boot 3](https://spring.io/projects/spring-boot) (Web, Validation, Actuator)
- Maven (com Maven Wrapper)

## Pré-requisitos

- JDK 21+
- Não é necessário ter o Maven instalado — o projeto usa o Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Inicialização

1. Clone o repositório e entre na branch `inicializacao-projeto`:

   ```bash
   git clone https://github.com/MRanderle/monitoolring--backend.git
   cd monitoolring--backend
   git checkout inicializacao-projeto
   ```

2. Suba a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

   No Windows (PowerShell/cmd):

   ```bash
   mvnw.cmd spring-boot:run
   ```

3. Verifique se a API está no ar:

   ```
   GET http://localhost:8080/api/health
   ```

## Comandos úteis

| Comando                  | Descrição                          |
|---------------------------|--------------------------------------|
| `./mvnw spring-boot:run`  | Sobe a aplicação em modo dev         |
| `./mvnw test`             | Executa os testes                    |
| `./mvnw clean package`    | Gera o JAR em `target/`              |

## Configuração

As configurações da aplicação ficam em [`src/main/resources/application.yml`](src/main/resources/application.yml), incluindo porta do servidor (`8080` por padrão) e endpoints do Actuator expostos.

O CORS está liberado por padrão para `http://localhost:3000` (frontend em desenvolvimento) — veja [`CorsConfig`](src/main/java/com/monitoolring/api/config/CorsConfig.java).

## Estrutura

```
src/main/java/com/monitoolring/api/
├── ApiApplication.java     # Entry point
├── config/                 # Configurações (ex.: CORS)
└── controller/              # Endpoints REST
```
