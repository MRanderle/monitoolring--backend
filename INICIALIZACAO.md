# Inicialização — Monitoolring Backend

Guia completo para colocar a API do Monitoolring rodando, localmente ou via Docker.

## Visão geral

| Item        | Valor                                  |
|-------------|------------------------------------------|
| Linguagem   | Java 21                                  |
| Framework   | Spring Boot 3.3.2                        |
| Build tool  | Maven (via Maven Wrapper, não precisa instalar Maven) |
| Porta       | `8080`                                   |
| Health check| `GET /api/health`                        |

## Opção 1 — Rodando localmente (sem Docker)

### Pré-requisitos

- JDK 21 ou superior instalado (`java -version` para conferir)
- Git

### Passo a passo

1. Clone o repositório e vá para a branch de trabalho:

   ```bash
   git clone https://github.com/MRanderle/monitoolring--backend.git
   cd monitoolring--backend
   git checkout inicializacao-projeto
   ```

2. Suba a aplicação com o Maven Wrapper (não precisa ter Maven instalado):

   ```bash
   # Linux / macOS / Git Bash
   ./mvnw spring-boot:run

   # Windows (PowerShell / cmd)
   mvnw.cmd spring-boot:run
   ```

3. Aguarde o log `Started ApiApplication` e teste:

   ```bash
   curl http://localhost:8080/api/health
   ```

   Resposta esperada:

   ```json
   { "status": "UP", "service": "monitoolring-api", "timestamp": "..." }
   ```

### Outros comandos úteis

| Comando                  | Descrição                     |
|---------------------------|----------------------------------|
| `./mvnw test`             | Executa os testes                |
| `./mvnw clean package`    | Gera o JAR em `target/`          |
| `java -jar target/*.jar`  | Roda o JAR já empacotado         |

## Opção 2 — Rodando com Docker

### Pré-requisitos

- Docker instalado (Docker Desktop no Windows/Mac, ou Docker Engine no Linux)
- Docker Compose (já incluso no Docker Desktop)

### Passo a passo

1. Clone o repositório (se ainda não tiver feito):

   ```bash
   git clone https://github.com/MRanderle/monitoolring--backend.git
   cd monitoolring--backend
   git checkout feature/docker   # ou a branch/tag que contém os arquivos Docker
   ```

2. Crie a rede externa compartilhada com o frontend (só precisa fazer uma vez por máquina):

   ```bash
   docker network create monitoolring-network
   ```

   Se a rede já existir, o comando retorna um erro inofensivo (`network already exists`) — pode ignorar.

3. Suba o backend:

   ```bash
   docker compose up --build
   ```

   Isso vai:
   - Buildar a imagem (`monitoolring-backend:local`) usando o [Dockerfile](Dockerfile) multi-stage (compila com Maven, roda com JRE)
   - Subir o container `monitoolring-backend` na porta `8080`
   - Conectar o container à rede `monitoolring-network`

4. Teste:

   ```bash
   curl http://localhost:8080/api/health
   ```

5. Para rodar em segundo plano:

   ```bash
   docker compose up -d --build
   ```

6. Para parar:

   ```bash
   docker compose down
   ```

### Rodando apenas com `docker build` / `docker run` (sem compose)

```bash
docker build -t monitoolring-backend .
docker run -p 8080:8080 monitoolring-backend
```

> Nesse modo o container **não** entra na rede `monitoolring-network`, então o frontend não vai conseguir chamá-lo pelo nome do serviço — use o compose se for rodar os dois juntos.

## Rodando backend + frontend juntos

O [frontend](https://github.com/MRanderle/monitoolring--frontend) tem seu próprio `docker-compose.yml` e entra na mesma rede externa `monitoolring-network`. Ordem sugerida:

```bash
docker network create monitoolring-network

# Terminal 1
cd monitoolring--backend
docker compose up --build

# Terminal 2
cd monitoolring--frontend
docker compose up --build
```

Depois disso:

- Backend: `http://localhost:8080/api/health`
- Frontend: `http://localhost:3000`
- De dentro do container do frontend, o backend é acessível em `http://monitoolring-backend:8080`

## Estrutura do projeto

```
.
├── Dockerfile                  # Build multi-stage (Maven -> JRE)
├── docker-compose.yml          # Sobe o backend na rede compartilhada
├── mvnw / mvnw.cmd              # Maven Wrapper
├── docs/                        # PRD e convenções de API
└── src/main/java/com/monitoolring/api/
    ├── ApiApplication.java      # Entry point
    ├── config/                  # Configurações (ex.: CORS)
    ├── controller/               # Endpoints REST
    ├── dto/                      # Objetos de transferência de dados
    ├── service/                  # Regras de negócio
    ├── repository/               # Acesso a dados
    └── domain/                   # Entidades/modelo de domínio
```

## Branches do repositório

| Branch                  | Propósito                                                |
|--------------------------|-------------------------------------------------------------|
| `master`                 | Branch protegida, integrada via Pull Request                |
| `inicializacao-projeto`  | Scaffolding inicial do projeto                               |
| `feature/docker`         | Arquivos de containerização (Dockerfile, docker-compose)     |

## Troubleshooting

- **`./mvnw: Permission denied`**: rode `chmod +x mvnw` antes.
- **Porta `8080` já em uso**: outro processo está usando a porta. Pare-o ou mude a porta no `docker-compose.yml` (`"8081:8080"`, por exemplo) e no `application.yml`.
- **`network monitoolring-network not found`**: rode `docker network create monitoolring-network` antes de subir o compose.
