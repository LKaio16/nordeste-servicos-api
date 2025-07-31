#!/bin/bash

echo "🔍 Verificando uso de memória da aplicação..."

# Verifica se a aplicação está rodando
if pgrep -f "nordeste-servicos" > /dev/null; then
    echo "✅ Aplicação está rodando"
    
    # Obtém o PID da aplicação
    PID=$(pgrep -f "nordeste-servicos")
    echo "📊 PID: $PID"
    
    # Verifica uso de memória
    echo "💾 Uso de memória:"
    ps -p $PID -o pid,ppid,%mem,%cpu,cmd --no-headers
    
    # Verifica se está usando as configurações otimizadas
    echo ""
    echo "🔧 Verificando configurações Java:"
    ps -p $PID -o cmd --no-headers | grep -o "Xmx[0-9]*m" || echo "❌ Configurações de memória não encontradas"
    
else
    echo "❌ Aplicação não está rodando"
    echo "💡 Para iniciar: mvn spring-boot:run"
fi

echo ""
echo "📈 Dicas para economizar:"
echo "- Use perfil 'prod': SPRING_PROFILES_ACTIVE=prod"
echo "- Configure JAVA_OPTS: -Xmx256m -Xms128m"
echo "- Monitore logs: tail -f logs/application.log" 