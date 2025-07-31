#!/bin/bash

echo "🚀 Iniciando build da aplicação Nordeste Serviços..."

# Limpa e compila o projeto
echo "📦 Compilando o projeto..."
mvn clean package -DskipTests

# Verifica se o build foi bem-sucedido
if [ $? -eq 0 ]; then
    echo "✅ Build concluído com sucesso!"
    echo "📁 Arquivo JAR gerado em: target/nordeste-servicos-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "🎯 Próximos passos para deploy no Railway:"
    echo "1. Faça commit e push das alterações"
    echo "2. Configure as variáveis de ambiente no Railway"
    echo "3. Adicione um serviço PostgreSQL no Railway"
    echo "4. O deploy será iniciado automaticamente"
else
    echo "❌ Erro no build. Verifique os logs acima."
    exit 1
fi 