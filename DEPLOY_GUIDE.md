# 🚀 Guia de Deploy - Nordeste Serviços API no Railway

## 📋 Pré-requisitos

- ✅ Conta no Railway (https://railway.app)
- ✅ Repositório Git configurado
- ✅ Java 17 instalado
- ✅ Maven instalado

## 🔧 Configuração Local

### 1. Build da Aplicação

```bash
# Execute o build
mvn clean package -DskipTests

# Ou use o script
./build.sh
```

### 2. Verifique se o JAR foi gerado

```bash
ls target/nordeste-servicos-0.0.1-SNAPSHOT.jar
```

## 🚀 Deploy no Railway

### Passo 1: Conectar Repositório

1. Acesse https://railway.app
2. Faça login e crie um novo projeto
3. Selecione "Deploy from GitHub repo"
4. Conecte seu repositório

### Passo 2: Configurar Banco de Dados

1. No Railway, clique em "New Service"
2. Selecione "Database" → "PostgreSQL"
3. Aguarde a criação do banco
4. Copie as credenciais fornecidas

### Passo 3: Configurar Variáveis de Ambiente

No seu projeto no Railway, vá para a aba "Variables" e adicione:

```env
# Banco de Dados (substitua pelos valores do seu banco Railway)
DATABASE_URL=jdbc:postgresql://[host]:[port]/[database]
DATABASE_USERNAME=[username]
DATABASE_PASSWORD=[password]

# JWT (GERE UMA CHAVE SEGURA!)
JWT_SECRET=sua-chave-secreta-muito-longa-e-segura-aqui
JWT_EXPIRATION=36000000

# CORS (adicione os domínios do seu frontend)
CORS_ALLOWED_ORIGINS=https://seu-frontend.vercel.app,https://seu-frontend.netlify.app

# Configurações da Aplicação
SPRING_PROFILES_ACTIVE=prod
SHOW_SQL=false
```

### Passo 4: Deploy Automático

1. Faça commit e push das alterações:
```bash
git add .
git commit -m "Configuração para deploy no Railway"
git push origin main
```

2. O Railway detectará automaticamente o Dockerfile e iniciará o deploy

## 📁 Arquivos de Configuração Criados

- `Dockerfile` - Containerização da aplicação
- `railway.toml` - Configuração do Railway
- `railway.json` - Configuração alternativa do Railway
- `system.properties` - Versão do Java
- `.dockerignore` - Otimização do build
- `build.sh` - Script de build
- `env.example` - Exemplo de variáveis de ambiente

## 🔍 Verificação do Deploy

### 1. Logs do Railway
- Acesse os logs no painel do Railway
- Verifique se não há erros

### 2. Health Check
- Acesse: `https://seu-app.railway.app/actuator/health`
- Deve retornar status "UP"

### 3. Swagger UI
- Acesse: `https://seu-app.railway.app/swagger-ui.html`
- Verifique se a API está funcionando

## 🛠️ Troubleshooting

### Erro de Build
- Verifique se o Java 17 está configurado
- Execute `mvn clean package -DskipTests` localmente

### Erro de Conexão com Banco
- Verifique as variáveis de ambiente no Railway
- Confirme se o banco PostgreSQL está ativo

### Erro de Porta
- O Railway define automaticamente a variável `PORT`
- A aplicação está configurada para usar essa variável

### Erro de Memória
- Ajuste as variáveis `JAVA_OPTS` no Dockerfile se necessário

## 📞 Suporte

Se encontrar problemas:

1. Verifique os logs no Railway
2. Teste localmente com as mesmas variáveis de ambiente
3. Consulte a documentação do Railway
4. Verifique se todas as dependências estão no `pom.xml`

## 🎉 Próximos Passos

Após o deploy bem-sucedido:

1. Configure o domínio personalizado (opcional)
2. Configure monitoramento e alertas
3. Configure backups automáticos do banco
4. Configure CI/CD para deploys automáticos

---

**🎯 Sua API estará disponível em:** `https://seu-app.railway.app` 