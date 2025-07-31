# Use uma imagem mais estável do OpenJDK
FROM eclipse-temurin:17-jre-alpine

# Instala curl para health check
RUN apk add --no-cache curl

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR da aplicação
COPY target/nordeste-servicos-0.0.1-SNAPSHOT.jar app.jar

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