# Multi-stage build para otimizar o tamanho final
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos de configuração do Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Baixa as dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Faz o build da aplicação
RUN mvn clean package -DskipTests

# Stage de produção
FROM eclipse-temurin:17-jre-alpine

# Instala curl para health check
RUN apk add --no-cache curl

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR do stage de build
COPY --from=build /app/target/nordeste-servicos-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Define variáveis de ambiente para otimização de memória
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -Djava.security.egd=file:/dev/./urandom"

# Script de inicialização com logs
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'echo "Starting application..."' >> /app/start.sh && \
    echo 'echo "JAVA_OPTS: $JAVA_OPTS"' >> /app/start.sh && \
    echo 'echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"' >> /app/start.sh && \
    echo 'java $JAVA_OPTS -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Comando para executar a aplicação
ENTRYPOINT ["/app/start.sh"] 