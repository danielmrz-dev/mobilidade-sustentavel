# Mobilidade Sustentavel API

API RESTful em **Java 21 + Spring Boot 3** para o tema ESG **5 - Mobilidade sustentavel**.

O usuario registra seus deslocamentos (bicicleta, onibus, metro, carro eletrico...) e a API calcula
quanto CO2 ele **deixou de emitir** em comparacao a um carro a combustao. O CO2 evitado vira **pontos**,
que podem ser trocados por **recompensas**. Empresas acompanham uma **meta mensal** de CO2 evitado
e o **ranking** dos colaboradores.

| Subtema da atividade | Como a API atende |
|---|---|
| Incentivar e monitorar transportes sustentaveis | Registro de deslocamentos, historico e indicadores |
| Impacto da substituicao de veiculos a combustao | Calculo de CO2 emitido x evitado por modal |
| Recompensas para empresas e individuos | Pontos, catalogo de recompensas, resgates e ranking por empresa |

## Integrantes

| Nome | RM |
|---|---|
| | |
| | |
| | |

## Tecnologias

- Java 21, Spring Boot 3.5 (Web, Data JPA, Validation, Security)
- Autenticacao stateless com **JWT** (jjwt) e senhas com **BCrypt**
- Banco **Oracle** (driver ojdbc11)
- **Flyway** para migracoes (`flyway-database-oracle`)
- **Docker** (Dockerfile multi-stage) e **Docker Compose** com Oracle Free
- Swagger / OpenAPI (springdoc) em `/swagger-ui.html`
- JUnit 5 + Mockito + AssertJ

## Arquitetura

Organizacao **por feature** (package-by-feature). Dentro de cada feature: Controller -> Service -> Repository,
com DTOs (records) separados das entidades.

```
Cliente (Postman / Insomnia / Front-end)
   |  Authorization: Bearer <JWT>
   v
JwtAuthenticationFilter -> SecurityConfig (regras por rota e por perfil)
   v
Controller   recebe DTO com @Valid e devolve DTO + status HTTP
   v
Service      regras de negocio (calculo de CO2, pontos, resgates) com @Transactional
   v
Repository   Spring Data JPA (consultas derivadas, JPQL e projecoes)
   v
Oracle  <--  Flyway (db/migration)

GlobalExceptionHandler (@RestControllerAdvice) padroniza todos os erros
```

```
src/main/java/br/com/grupo/mobilidade
├── config/        SecurityConfig, OpenApiConfig, AdminInitializer, *Properties
├── security/      JwtService, JwtAuthenticationFilter, UserDetailsServiceImpl, handlers 401/403
├── shared/
│   ├── exception/   GlobalExceptionHandler, excecoes de negocio, ErroResponse
│   └── validation/  @SenhaForte (validacao customizada)
├── auth/          registro e login
├── usuario/       perfil, impacto individual e resgates do usuario
├── empresa/       CRUD de empresas, meta mensal e ranking
├── modal/         CRUD de modais de transporte e fatores de emissao
├── deslocamento/  registro de trajetos + CalculadoraImpacto + consultas agregadas
├── recompensa/    catalogo de recompensas e resgate de pontos
└── indicador/     painel geral de sustentabilidade
src/main/resources
├── application.yml
└── db/migration/  V1 (tabelas), V2 (modais), V3 (empresas e recompensas)
```

### Modelo de dados

```
TB_MOB_EMPRESA 1 ──< TB_MOB_USUARIO 1 ──< TB_MOB_DESLOCAMENTO >── 1 TB_MOB_MODAL
                           1
                           │
                           ^
                     TB_MOB_RESGATE >── 1 TB_MOB_RECOMPENSA
```

### Regra de negocio central

```
CO2 emitido (kg) = distancia (km) x fator do modal (g/km) / 1000
CO2 evitado (kg) = distancia (km) x (fator do carro a combustao - fator do modal) / 1000   (nunca negativo)
Pontos           = CO2 evitado (kg) x 10   (arredondado para baixo; configuravel)
```

Exemplo: 10 km de bicicleta (0 g/km) em vez de carro (150 g/km) = 1,5 kg de CO2 evitado = 15 pontos.

> Os fatores de emissao do arquivo `V2__inserir_modais.sql` sao **aproximados** para o MVP.
> **TODO do grupo:** revisar os valores com uma fonte oficial e cita-la aqui.

Outras regras implementadas:

- Ao **editar** um deslocamento, o impacto e recalculado e os pontos sao ajustados; ao **remover**, os pontos sao estornados.
  Se os pontos ja foram gastos em resgates, a operacao e bloqueada (422).
- Modal **inativo** nao aceita novos registros. O DELETE de modal e uma **exclusao logica** (preserva o historico).
- Resgate valida **estoque e saldo** e usa `SELECT ... FOR UPDATE` para evitar dois resgates da ultima unidade.
- Usuario so enxerga os **proprios** deslocamentos (acessar o de outra pessoa responde 404).
- Empresa com usuarios vinculados e recompensa ja resgatada nao podem ser removidas (409).

## Como executar

### Opcao 1 - Docker Compose (recomendado)

Pre-requisito: Docker Desktop.

```bash
cp .env.example .env        # opcional: ajuste senhas e chave JWT
docker compose up --build
```

- O Oracle leva de 1 a 3 minutos para ficar pronto na primeira vez. A API so sobe depois que o
  healthcheck do banco passa.
- Na inicializacao o **Flyway** cria as tabelas e insere os dados iniciais, e o `AdminInitializer`
  cria o usuario ADMIN.
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

Para parar: `docker compose down` (os dados ficam no volume). Para apagar tudo: `docker compose down -v`.

### Opcao 2 - Oracle da faculdade (ou outro Oracle ja existente)

Gere a imagem e rode apenas a API, apontando para o banco:

```bash
docker build -t mobilidade-api .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:oracle:thin:@HOST:1521/SERVICE \
  -e DB_USERNAME=seu_usuario \
  -e DB_PASSWORD=sua_senha \
  mobilidade-api
```

As tabelas usam o prefixo `TB_MOB_` e o Flyway esta com `baseline-on-migrate`, entao o schema pode ter outras tabelas.

### Opcao 3 - Rodar local pela IDE

Suba so o banco com `docker compose up oracle` e rode `MobilidadeApplication` (ou `mvn spring-boot:run`).
Os valores padrao do `application.yml` ja apontam para `localhost:1521/FREEPDB1`.

Testes unitarios (nao precisam de banco): `mvn test`

## Variaveis de ambiente

| Variavel | Padrao | Descricao |
|---|---|---|
| `DB_URL` | `jdbc:oracle:thin:@localhost:1521/FREEPDB1` | URL JDBC do Oracle |
| `DB_USERNAME` / `DB_PASSWORD` | `mobilidade` / `mobilidade123` | Usuario do schema |
| `JWT_SECRET` | chave de desenvolvimento | Chave Base64 (minimo 32 bytes) |
| `JWT_EXPIRATION_MS` | `7200000` (2h) | Validade do token |
| `ADMIN_EMAIL` / `ADMIN_SENHA` | `admin@mobilidade.com` / `Admin@123` | Primeiro ADMIN |
| `PONTOS_POR_KG_CO2` | `10` | Pontos por kg de CO2 evitado |

## Seguranca

- Rotas publicas: `POST /auth/registro`, `POST /auth/login` e o Swagger.
- Todas as outras exigem `Authorization: Bearer <token>`.
- Perfis: **USER** (cadastro publico) e **ADMIN** (criado na inicializacao).
- Cadastros administrativos (POST/PUT/DELETE de modais, empresas e recompensas e `GET /usuarios`) exigem ADMIN.
- Respostas: **401** sem token ou token invalido/expirado; **403** perfil sem permissao.

## Endpoints

| Metodo | Rota | Acesso | Descricao |
|---|---|---|---|
| POST | `/auth/registro` | publico | Cadastra usuario (perfil USER) |
| POST | `/auth/login` | publico | Devolve o token JWT |
| GET | `/usuarios/me` | logado | Perfil e saldo de pontos |
| GET | `/usuarios/me/impacto` | logado | CO2 emitido, evitado e % de reducao do usuario |
| GET | `/usuarios/me/resgates` | logado | Historico de resgates |
| GET | `/usuarios` | ADMIN | Lista usuarios (paginado) |
| GET | `/modais` | logado | Lista modais (`?status=ATIVO`) |
| GET | `/modais/{id}` | logado | Detalha modal |
| POST | `/modais` | ADMIN | Cadastra modal |
| PUT | `/modais/{id}` | ADMIN | Atualiza modal |
| DELETE | `/modais/{id}` | ADMIN | Inativa modal (exclusao logica) |
| POST | `/deslocamentos` | logado | Registra trajeto, calcula CO2 e credita pontos |
| GET | `/deslocamentos` | logado | Lista os proprios (`?de=2026-10-01&ate=2026-10-31&page=0&size=10`) |
| GET | `/deslocamentos/{id}` | dono | Detalha |
| PUT | `/deslocamentos/{id}` | dono | Atualiza e recalcula |
| DELETE | `/deslocamentos/{id}` | dono | Remove e estorna pontos |
| GET | `/empresas` | logado | Lista empresas |
| GET | `/empresas/{id}` | logado | Detalha empresa |
| GET | `/empresas/{id}/ranking` | logado | Progresso da meta do mes + ranking dos colaboradores |
| POST | `/empresas` | ADMIN | Cadastra (CNPJ validado) |
| PUT | `/empresas/{id}` | ADMIN | Atualiza |
| DELETE | `/empresas/{id}` | ADMIN | Remove (sem usuarios vinculados) |
| GET | `/recompensas` | logado | Catalogo (`?disponiveis=true`) |
| GET | `/recompensas/{id}` | logado | Detalha |
| POST | `/recompensas` | ADMIN | Cadastra |
| PUT | `/recompensas/{id}` | ADMIN | Atualiza |
| DELETE | `/recompensas/{id}` | ADMIN | Remove (se nunca resgatada) |
| POST | `/recompensas/{id}/resgates` | logado | Troca pontos pela recompensa |
| GET | `/indicadores/impacto` | logado | Totais da plataforma, impacto por modal e ranking de empresas |

Listagens sao paginadas e aceitam `page`, `size` e `sort` (ex.: `?sort=nome,asc`).

## Validacao e tratamento de erros

- Bean Validation nos DTOs (`@NotBlank`, `@Email`, `@DecimalMin`, `@PastOrPresent`, `@Digits` etc.).
- `@CNPJ` do Hibernate Validator confere os digitos verificadores.
- `@SenhaForte`: validacao **customizada** (maiuscula, minuscula, numero e caractere especial).
- Todo erro volta no mesmo formato:

```json
{
  "timestamp": "2026-10-20T10:30:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Existem campos invalidos na requisicao",
  "caminho": "/deslocamentos",
  "campos": [
    { "campo": "data", "mensagem": "A data do deslocamento nao pode estar no futuro" }
  ]
}
```

| Status | Quando |
|---|---|
| 400 | Campos invalidos, JSON malformado, enum ou parametro invalido |
| 401 | Sem token, token invalido/expirado, login incorreto |
| 403 | Perfil sem permissao |
| 404 | Recurso inexistente ou de outro usuario |
| 409 | Duplicidade (e-mail, CNPJ, nome de modal) ou registro em uso |
| 422 | Regra de negocio (saldo insuficiente, sem estoque, modal inativo) |

## Collection (Postman / Insomnia)

O arquivo `collection/mobilidade-sustentavel.postman_collection.json` tem **43 requisicoes**, incluindo
cenarios de erro. Ele pode ser importado no Postman ou no Insomnia (Import > arquivo).

1. Importe a collection.
2. Rode a pasta **1. Autenticacao** primeiro: os logins salvam `{{token}}` (usuario) e `{{tokenAdmin}}` (admin)
   automaticamente.
3. Rode as demais pastas na ordem (ou use o **Runner** para executar tudo de uma vez).

Usuario ADMIN padrao: `admin@mobilidade.com` / `Admin@123`
