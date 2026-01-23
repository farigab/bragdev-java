# 🏆 BragDoc

> Sistema de documentação de conquistas profissionais com integração GitHub e análise por IA

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📋 Sobre o Projeto

BragDoc é uma aplicação para documentar e gerenciar conquistas profissionais, permitindo que desenvolvedores mantenham um registro organizado de suas realizações. O sistema oferece:

- 📝 **Gestão de Conquistas**: Crie, edite e organize suas realizações profissionais
- 🔗 **Integração GitHub**: Importe automaticamente PRs, commits e issues
- 🤖 **Relatórios com IA**: Gere análises profissionais usando Google Gemini
- 📊 **Estatísticas**: Visualize seu progresso e evolução profissional
- 🔐 **OAuth GitHub**: Autenticação segura via GitHub

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture**, organizando o código em 4 camadas bem definidas:

```
┌─────────────────────────────────────────────────────────┐
│                    Interface Layer                       │
│              (Controllers, DTOs, Config)                 │
└─────────────────┬───────────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────────┐
│                  Application Layer                       │
│         (Use Cases, Application Services)                │
└─────────────────┬───────────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────────┐
│                    Domain Layer                          │
│      (Entities, Value Objects, Domain Services)          │
└─────────────────┬───────────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────────┐
│               Infrastructure Layer                       │
│    (Repositories, External APIs, Persistence)            │
└─────────────────────────────────────────────────────────┘
```

### Benefícios da Arquitetura

- ✅ **Testabilidade**: Camadas domain e application totalmente testáveis
- ✅ **Manutenibilidade**: Código organizado e fácil de localizar
- ✅ **Independência**: Domain livre de frameworks
- ✅ **Flexibilidade**: Fácil trocar implementações de infraestrutura

## 🚀 Tecnologias

### Backend
- **Java 25** - Linguagem de programação
- **Spring Boot 4.1.0-SNAPSHOT** - Framework web
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados
- **JWT** - Autenticação
- **OAuth 2.0** - GitHub Authentication

### Integrações
- **GitHub API** - Importação de contribuições
- **Google Gemini** - Geração de relatórios com IA
- **Kohsuke GitHub API** - Cliente Java para GitHub

### Ferramentas
- **Lombok** - Redução de boilerplate
- **Springdoc OpenAPI** - Documentação da API
- **dotenv-java** - Gerenciamento de variáveis de ambiente

## 📦 Estrutura do Projeto

```
src/main/java/bragdoc/
│
├── domain/                # Camada de Domínio
│   ├── achievement/       # Módulo de conquistas
│   ├── user/              # Módulo de usuários
│   ├── report/            # Módulo de relatórios
│   ├── github/            # Módulo GitHub
│   └── shared/            # Compartilhado
│
├── application/           # Camada de Aplicação
│   ├── achievement/       # Use Cases de conquistas
│   ├── user/              # Use Cases de usuários
│   ├── report/            # Use Cases de relatórios
│   └── github/            # Use Cases GitHub
│
├── infrastructure/        # Camada de Infraestrutura
│   ├── persistence/       # Persistência (JPA)
│   ├── integration/       # APIs externas
│   └── security/          # Segurança (JWT)
│
└── interfaces/            # Camada de Interface
    ├── api/               # Controllers REST
    └── config/            # Configurações Spring
```

## 🔧 Configuração

### Pré-requisitos

- Java 21+
- PostgreSQL 15+
- Maven 3.8+
- Conta GitHub (para OAuth)
- Chave API do Google Gemini

### Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/bragdoc
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
DB_DRIVER=org.postgresql.Driver
DB_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# GitHub OAuth
GITHUB_OAUTH_CLIENT_ID=seu_client_id
GITHUB_OAUTH_CLIENT_SECRET=seu_client_secret
GITHUB_OAUTH_REDIRECT_URI=http://localhost:8080/api/auth/callback
OAUTH_FRONTEND_REDIRECT=http://localhost:4200

# JWT
JWT_SECRET=seu_secret_jwt_aqui_minimo_256_bits

# Gemini AI
GEMINI_API_KEY=sua_chave_gemini
```

### Banco de Dados

1. Crie o banco de dados:
```sql
CREATE DATABASE bragdoc;
```

2. Execute as migrations (Spring Boot irá criar as tabelas automaticamente na primeira execução)

### GitHub OAuth

1. Acesse https://github.com/settings/developers
2. Crie uma nova OAuth App
3. Configure:
   - **Homepage URL**: `http://localhost:8080`
   - **Callback URL**: `http://localhost:8080/api/auth/callback`
4. Copie o Client ID e Client Secret para o `.env`

### Google Gemini

1. Acesse https://makersuite.google.com/app/apikey
2. Crie uma nova API Key
3. Copie a chave para o `.env`

## 🏃 Executando o Projeto

### Desenvolvimento

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/bragdoc.git
cd bragdoc

# Configure as variáveis de ambiente
cp .env.example .env
# Edite o .env com suas credenciais

# Execute o projeto
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`

### Produção

```bash
# Build
./mvnw clean package -DskipTests

# Execute o JAR
java -jar target/bragdoc-0.0.1-SNAPSHOT.jar
```

## 📚 Documentação da API

A documentação interativa está disponível via Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

### Principais Endpoints

#### Autenticação
- `GET /api/auth/github` - Redireciona para OAuth GitHub
- `GET /api/auth/callback` - Callback OAuth
- `POST /api/auth/logout` - Logout
- `POST /api/auth/github/token` - Salvar token GitHub
- `DELETE /api/auth/github/token` - Remover token GitHub

#### Usuários
- `GET /api/user` - Obter usuário atual

#### Conquistas
- `GET /api/achievements` - Listar conquistas
- `POST /api/achievements` - Criar conquista
- `GET /api/achievements/{id}` - Obter conquista
- `PUT /api/achievements/{id}` - Atualizar conquista
- `DELETE /api/achievements/{id}` - Deletar conquista
- `GET /api/achievements/search?keyword={keyword}` - Buscar conquistas
- `GET /api/achievements/category/{category}` - Filtrar por categoria
- `GET /api/achievements/date-range?startDate={start}&endDate={end}` - Filtrar por período

#### Relatórios
- `GET /api/reports/summary` - Resumo geral
- `GET /api/reports/by-category?category={category}` - Relatório por categoria
- `GET /api/reports/by-period?startDate={start}&endDate={end}` - Relatório por período
- `GET /api/reports/ai-summary?reportType={type}` - Relatório com IA
- `GET /api/reports/ai-github-analysis` - Análise GitHub com IA

#### GitHub
- `POST /api/github/import/repositories` - Listar repositórios
- `POST /api/github/import` - Importar dados

## 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes de uma camada específica
./mvnw test -Dtest="**/*UseCaseTest"

# Executar com cobertura
./mvnw test jacoco:report
```

## 📖 Exemplos de Uso

### Criar uma Conquista

```bash
curl -X POST http://localhost:8080/api/achievements \
  -H "Content-Type: application/json" \
  -H "Cookie: token=seu_jwt_token" \
  -d '{
    "title": "Implementação de Clean Architecture",
    "description": "Refatoração completa do projeto seguindo Clean Architecture",
    "category": "Arquitetura de Software",
    "date": "2025-01-22"
  }'
```

### Gerar Relatório com IA

```bash
curl -X GET "http://localhost:8080/api/reports/ai-summary?reportType=executive" \
  -H "Cookie: token=seu_jwt_token"
```

### Importar Dados do GitHub

```bash
curl -X POST http://localhost:8080/api/github/import \
  -H "Content-Type: application/json" \
  -H "Cookie: token=seu_jwt_token" \
  -d '{
    "repositories": ["owner/repo1", "owner/repo2"],
    "dataInicio": "2024-01-01T00:00:00",
    "dataFim": "2025-01-22T23:59:59"
  }'
```

## 🎨 Tipos de Relatórios

### Executive (Executivo)
Relatório focado em impacto profissional, ideal para currículos e avaliações de desempenho.

### Technical (Técnico)
Análise técnica detalhada das tecnologias, padrões e complexidade das implementações.

### Timeline (Linha do Tempo)
Narrativa cronológica mostrando a evolução e progressão de carreira.

### GitHub
Análise específica de contribuições open source e atividade no GitHub.

## 🔐 Segurança

- **Autenticação**: OAuth 2.0 via GitHub
- **Autorização**: JWT com validação em cada requisição
- **Cookies**: HttpOnly, Secure, SameSite=Strict
- **Validação**: Validação de entrada em todas as camadas
- **CORS**: Configurado para origens específicas

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código

- Seguir Clean Architecture
- Use nomes em português para domínio
- Mantenha Use Cases pequenos e focados
- Adicione testes para novas funcionalidades
- Domain layer deve ser livre de frameworks

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Autores

- **Gabriel Farias** - **Desenvolvedor Principal** - [farigab](https://github.com/farigab)

## 🙏 Agradecimentos

- Spring Boot team
- GitHub API
- Google Gemini
- Comunidade Open Source

---

⭐️ **Dica**: Mantenha seu BragDoc sempre atualizado! Documente suas conquistas regularmente para ter um registro completo de sua evolução profissional.

**Feito com ❤️ por desenvolvedor, para desenvolvedores**
