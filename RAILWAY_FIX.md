# Correção do Erro de Build no Railway

## Problema Resolvido
```
ERROR: failed to build: failed to solve: failed to compute cache key: failed to calculate checksum of ref xvbes8d5xr4vxjkqebxl3910o::wcoihm0zxsw745e4yixba5lvv: "/target/nordeste-servicos-0.0.1-SNAPSHOT.jar": not found
```

## Solução Aplicada

### 1. Removido Dockerfile
- O Railway estava tentando usar Dockerfile em vez do Nixpacks
- Dockerfile tentava copiar JAR que não existia durante o build

### 2. Removido .dockerignore
- Arquivo desnecessário já que não usamos mais Docker

### 3. Corrigido .railwayignore
- Removido `target/` da lista de ignorados
- Nixpacks precisa da pasta target para gerar o JAR

### 4. Configuração Final
- **Builder:** NIXPACKS (não Docker)
- **Build:** `mvn clean package -DskipTests`
- **Start:** `java -jar target/nordeste-servicos-0.0.1-SNAPSHOT.jar`

## Como Funciona Agora

1. Railway detecta projeto Maven
2. Nixpacks instala Maven + OpenJDK 17
3. Executa build Maven gerando JAR na pasta target/
4. Executa o JAR gerado automaticamente

## Próximos Passos

1. Commit das alterações
2. Push para o repositório
3. Railway fará build automático sem erros
