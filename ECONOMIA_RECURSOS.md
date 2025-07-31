# 💰 Guia de Economia de Recursos - Railway $5

## 🎯 Objetivo
Configurar sua API para consumir o mínimo de recursos possível e durar o mês inteiro com o plano de $5 do Railway.

## 📊 Otimizações Implementadas

### 1. **Memória Java Reduzida**
- **Antes**: 512MB heap + 256MB stack
- **Agora**: 256MB heap + 128MB stack
- **Economia**: ~50% de memória

### 2. **Imagem Docker Otimizada**
- **Antes**: `openjdk:17-jdk-slim` (maior)
- **Agora**: `openjdk:17-jre-slim` (menor)
- **Economia**: ~100MB de espaço

### 3. **Pool de Conexões Mínimo**
- **Antes**: 10 conexões
- **Agora**: 3 conexões máximas
- **Economia**: Menos uso de CPU e memória

### 4. **Logging Reduzido**
- **Antes**: INFO level
- **Agora**: ERROR/WARN level
- **Economia**: Menos I/O e processamento

### 5. **Upload de Arquivos Limitado**
- **Antes**: 10MB
- **Agora**: 2MB
- **Economia**: Menos armazenamento e processamento

## 🔧 Configurações Específicas

### Variáveis de Ambiente para Railway
```env
# Configurações de memória
JAVA_OPTS=-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200

# Perfil de produção
SPRING_PROFILES_ACTIVE=prod

# Logging mínimo
LOGGING_LEVEL_ROOT=ERROR
LOGGING_LEVEL_COM_CODAGIS=WARN
```

### Configurações do Banco
```env
# Pool de conexões mínimo
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=3
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=1
```

## 📈 Monitoramento de Uso

### Verificar Uso no Railway
1. Acesse o painel do Railway
2. Vá para a aba "Metrics"
3. Monitore:
   - **CPU Usage**: Deve ficar abaixo de 50%
   - **Memory Usage**: Deve ficar abaixo de 300MB
   - **Network**: Deve ser baixo

### Endpoints de Monitoramento
- `GET /actuator/health` - Status da aplicação
- Logs no Railway para verificar erros

## 🚨 Dicas para Economizar Mais

### 1. **Desabilitar Funcionalidades Desnecessárias**
- Swagger UI em produção
- Logs detalhados
- Cache de segundo nível

### 2. **Otimizar Consultas**
- Use paginação em todas as listas
- Limite resultados por página
- Use índices no banco de dados

### 3. **Configurações de Timeout**
- Conexões de banco: 20s
- Sessões: 30 minutos
- Health check: 5s

### 4. **Backup e Limpeza**
- Configure backup automático do banco
- Limpe logs antigos
- Monitore uso de disco

## ⚠️ Limitações com Plano $5

### Recursos Disponíveis
- **CPU**: Limitado
- **RAM**: ~512MB
- **Disco**: ~1GB
- **Rede**: Limitado

### Recomendações
1. **Não use para processamento pesado**
2. **Limite uploads de arquivos**
3. **Use cache quando possível**
4. **Monitore uso constantemente**

## 🔄 Escalabilidade

### Se Precisar de Mais Recursos
1. **Upgrade para plano $10**: Dobra os recursos
2. **Otimize código**: Melhore consultas
3. **Use CDN**: Para arquivos estáticos
4. **Cache externo**: Redis/Memcached

## 📊 Estimativa de Custo

### Com Otimizações
- **Uso médio**: ~200-300MB RAM
- **CPU**: ~20-30%
- **Duração estimada**: 30 dias
- **Custo**: $5/mês

### Sem Otimizações
- **Uso médio**: ~500-800MB RAM
- **CPU**: ~50-70%
- **Duração estimada**: 15-20 dias
- **Custo**: $10-15/mês

## 🎯 Checklist de Deploy

- [ ] Configurações de memória aplicadas
- [ ] Perfil `prod` ativo
- [ ] Pool de conexões reduzido
- [ ] Logging mínimo configurado
- [ ] Upload de arquivos limitado
- [ ] Health check configurado
- [ ] Monitoramento ativo

---

**💡 Dica**: Monitore o uso nos primeiros dias e ajuste conforme necessário! 