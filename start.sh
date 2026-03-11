#!/bin/sh
# Garante que PORT seja usado (Railway injeta em runtime)
export PORT=${PORT:-8080}
exec java -Dserver.port=$PORT -Dserver.address=0.0.0.0 \
  -Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -jar target/nordeste-servicos-0.0.1-SNAPSHOT.jar
