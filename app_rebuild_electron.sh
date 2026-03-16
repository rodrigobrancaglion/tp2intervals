#!/bin/bash

# 1. Definir caminhos baseados na raiz
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
BOOT_DIR="$BASE_DIR/boot"
ELECTRON_DIR="$BASE_DIR/electron"

echo "Limpando processos antigos..."
killall java 2>/dev/null || true

# 2. Compilar o Backend
echo "Entrando na pasta do Backend: $BOOT_DIR"
cd "$BOOT_DIR"

if [ -f "./gradlew" ]; then
    echo "Executando Gradle bootJar..."
    chmod +x ./gradlew
    # Rodamos a task localmente
    ./gradlew clean bootJar
else
    echo "❌ ERRO: gradlew não encontrado em $BOOT_DIR"
    exit 1
fi

# 3. Mover o JAR para o Electron
echo "Organizando artefatos..."
mkdir -p "$ELECTRON_DIR/artifact"
rm -f "$ELECTRON_DIR/artifact/boot.jar"

# Pegamos o JAR que NÃO tem "plain" no nome
# O xargs garante que pegamos o caminho limpo
JAR_REAL=$(ls build/libs/*.jar | grep -v "plain" | head -n 1)

if [ -n "$JAR_REAL" ]; then
    cp "$JAR_REAL" "$ELECTRON_DIR/artifact/boot.jar"
    TAMANHO=$(du -m "$ELECTRON_DIR/artifact/boot.jar" | cut -f1)
    echo "✅ Sucesso: JAR de $TAMANHO MB copiado."
else
    echo "❌ ERRO: Nenhum JAR executável encontrado em $BOOT_DIR/build/libs/"
    exit 1
fi

# 4. Gerar o App Electron
echo "Gerando App Electron..."
cd "$ELECTRON_DIR"
npm run build:unpack

echo "-------------------------------------------------------"
echo "Build Finalizado! O app atualizado está em: electron/dist/mac-arm64/tp2intervals.app"
echo "-------------------------------------------------------"

# 5. Instalar na pasta de Aplicativos do Mac
APP_NAME="tp2intervals.app"
APP_PATH="$ELECTRON_DIR/dist/mac-arm64/$APP_NAME"
DEST_PATH="/Applications/$APP_NAME"

echo "Instalando na pasta de Aplicativos..."

if [ -d "$APP_PATH" ]; then
    # Remove a versão antiga se existir para garantir uma cópia limpa
    rm -rf "$DEST_PATH"
    cp -R "$APP_PATH" "/Applications/"
    echo "✅ Sucesso! O app foi copiado para /Applications"

    # Opcional: Abre o aplicativo após a cópia
    # open "$DEST_PATH"
else
    echo "❌ ERRO: O arquivo .app não foi encontrado em $APP_PATH"
    exit 1
fi