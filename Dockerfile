syntax=docker/dockerfile:1

# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copia o código e builda o jar
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# Usuário não-root
RUN useradd -ms /bin/bash spring
USER spring

COPY --from=build /app/target/nordeste-servicos-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
