# Nordeste Serviços API

Sistema de Ordem de Serviços desenvolvido em Spring Boot.

## Deploy no Railway - OTIMIZADO PARA PLANO $5

### Pré-requisitos
- Conta no Railway (https://railway.app)
- Git configurado
- Java 17

### 💰 Otimizações para Economia
- **Memória**: Limitada a 256MB heap
- **CPU**: Configurações otimizadas
- **Banco**: Pool de 3 conexões máximo
- **Logs**: Nível ERROR/WARN apenas
- **Upload**: Limitado a 2MB por arquivo

### Passos para Deploy

1. **Faça o build da aplicação localmente:**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Conecte seu repositório ao Railway:**
   - Acesse https://railway.app
   - Faça login e crie um novo projeto
   - Selecione "Deploy from GitHub repo"
   - Conecte seu repositório

3. **Configure as variáveis de ambiente no Railway:**
   - Vá para a aba "Variables"
   - Adicione as seguintes variáveis:

   ```
   # Banco de Dados
   DATABASE_URL=jdbc:postgresql://[host]:[port]/[database]
   DATABASE_USERNAME=[username]
   DATABASE_PASSWORD=[password]
   
   # JWT
   JWT_SECRET=[sua-chave-secreta-muito-longa]
   JWT_EXPIRATION=36000000
   
   # CORS
   CORS_ALLOWED_ORIGINS=[origens-permitidas]
   
   # Configurações OTIMIZADAS para economia
   SPRING_PROFILES_ACTIVE=prod
   JAVA_OPTS=-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
   LOGGING_LEVEL_ROOT=ERROR
   LOGGING_LEVEL_COM_CODAGIS=WARN
   SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=3
   SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=1
   ```

4. **Configure o banco de dados:**
   - No Railway, adicione um serviço PostgreSQL
   - Copie as credenciais do banco para as variáveis de ambiente

5. **Deploy:**
   - O Railway detectará automaticamente o Dockerfile
   - O deploy será iniciado automaticamente após o push

### Estrutura de Arquivos para Deploy

- `Dockerfile` - Configuração do container
- `railway.toml` - Configuração do Railway
- `system.properties` - Versão do Java
- `.dockerignore` - Arquivos ignorados no build

### Endpoints Disponíveis

- **Health Check:** `GET /actuator/health`
- **Swagger UI:** `GET /swagger-ui.html`
- **API Base:** `GET /api/`

### Logs e Monitoramento

- Acesse os logs no painel do Railway
- Use o endpoint `/actuator/health` para verificar o status da aplicação

## Desenvolvimento Local

```bash
# Clone o repositório
git clone [url-do-repositorio]

# Execute a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080` 