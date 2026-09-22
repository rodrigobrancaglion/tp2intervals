#!/bin/bash
set -e

# ANSI Colors
BOLD="\033[1m"
CYAN="\033[36m"
BLUE="\033[34m"
GREEN="\033[32m"
YELLOW="\033[33m"
RED="\033[31m"
MAGENTA="\033[35m"
RESET="\033[0m"

# 1. Define paths relative to repository root
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
BOOT_DIR="$BASE_DIR/boot"
ELECTRON_DIR="$BASE_DIR/electron"
LOCAL_DB="$BASE_DIR/tp2intervals.sqlite"
APP_DATA_DIR="$HOME/Library/Application Support/tp2intervals"
APP_DATA_DB="$APP_DATA_DIR/tp2intervals.sqlite"

# Read version
APP_VERSION="1.0.0"
if [ -f "$BOOT_DIR/version" ]; then
    APP_VERSION=$(cat "$BOOT_DIR/version" | tr -d '[:space:]')
fi

# Print CLI Banner
echo -e "${CYAN}${BOLD}"
cat << "EOF"
  _______ _____ ___  _____ _____ _    _ 
 |__   __|  __ \__ \|_   _/ ____| |  | |
    | |  | |__) | ) | | || |    | |  | |
    | |  |  ___/ / /  | || |    | |  | |
    | |  | |    / /_ _| || |____| |__| |
    |_|  |_|   |____|_____\_____|\____/ 
EOF
echo -e "${RESET}"
echo -e "${BLUE}${BOLD}  TrainingPeaks & Third-Party -> Intervals.icu Sync${RESET}"
echo -e "${MAGENTA}  Version: ${APP_VERSION} ${RESET} | ${YELLOW}macOS Packager (with DB Sync)${RESET}"
echo -e "${CYAN}================================================================${RESET}"
echo ""

echo -e "${YELLOW}⚡ [1/6] Terminating running TP2Intervals app & background processes...${RESET}"
osascript -e 'quit app "tp2intervals"' 2>/dev/null || true
pkill -f "tp2intervals.app" 2>/dev/null || true
pkill -f "tp2intervals" 2>/dev/null || true
killall java 2>/dev/null || true
pkill -f "boot.jar" 2>/dev/null || true
sleep 1
echo -e "${GREEN}✔ Running app and background processes terminated.${RESET}"
echo ""

# 2. Database Synchronization (Optional - Asked at the beginning)
if [ -f "$LOCAL_DB" ]; then
    echo -e "${YELLOW}💾 [2/6] Database Synchronization Check${RESET}"
    echo -e "   Local database found: ${CYAN}$LOCAL_DB${RESET}"
    read -p "$(echo -e ${BOLD}"   Do you want to copy the local database into the App data directory? (y/N): "${RESET})" choice
    case "$choice" in
        [yY][eE][sS]|[yY])
            mkdir -p "$APP_DATA_DIR"
            if [ -f "$APP_DATA_DB" ]; then
                BACKUP_DB="$APP_DATA_DIR/tp2intervals_backup_$(date +%Y%m%d_%H%M%S).sqlite"
                cp "$APP_DATA_DB" "$BACKUP_DB"
                echo -e "   ${BLUE}📦 Existing App database backed up to: $BACKUP_DB${RESET}"
            fi
            cp "$LOCAL_DB" "$APP_DATA_DB"
            echo -e "   ${GREEN}✔ Local database copied to: $APP_DATA_DB${RESET}"
            ;;
        *)
            echo -e "   ${YELLOW}ℹ Skipping database copy. Existing App database will be preserved.${RESET}"
            ;;
    esac
    echo ""
else
    echo -e "${YELLOW}💾 [2/6] No local database found at $LOCAL_DB. Skipping sync.${RESET}"
    echo ""
fi

# 3. Build Backend
echo -e "${YELLOW}⚙️  [3/6] Building Spring Boot Backend (bootJar)...${RESET}"
cd "$BOOT_DIR"

if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    ./gradlew clean bootJar
    echo -e "${GREEN}✔ Backend built successfully.${RESET}"
else
    echo -e "${RED}❌ ERROR: gradlew not found in $BOOT_DIR${RESET}"
    exit 1
fi
echo ""

# 4. Move JAR to Electron artifacts
echo -e "${YELLOW}📦 [4/6] Organizing Electron artifacts...${RESET}"
mkdir -p "$ELECTRON_DIR/artifact"
rm -f "$ELECTRON_DIR/artifact/boot.jar"

JAR_REAL=$(ls build/libs/*.jar 2>/dev/null | grep -v "plain" | head -n 1 || true)

if [ -n "$JAR_REAL" ]; then
    cp "$JAR_REAL" "$ELECTRON_DIR/artifact/boot.jar"
    SIZE=$(du -m "$ELECTRON_DIR/artifact/boot.jar" | cut -f1)
    echo -e "${GREEN}✔ Executable JAR ($SIZE MB) copied to electron/artifact/boot.jar${RESET}"
else
    echo -e "${RED}❌ ERROR: No executable JAR found in $BOOT_DIR/build/libs/${RESET}"
    exit 1
fi
echo ""

# 5. Build Electron App
echo -e "${YELLOW}🚀 [5/6] Packaging Electron Application...${RESET}"
cd "$ELECTRON_DIR"
npm run build:unpack
echo -e "${GREEN}✔ Electron app packaged at: electron/dist/mac-arm64/tp2intervals.app${RESET}"
echo ""

# 6. Install to macOS Applications folder
APP_NAME="tp2intervals.app"
APP_PATH="$ELECTRON_DIR/dist/mac-arm64/$APP_NAME"
DEST_PATH="/Applications/$APP_NAME"

echo -e "${YELLOW}📂 [6/6] Installing to /Applications...${RESET}"

if [ -d "$APP_PATH" ]; then
    rm -rf "$DEST_PATH"
    cp -R "$APP_PATH" "/Applications/"
    echo -e "${GREEN}✔ App successfully installed to /Applications/$APP_NAME${RESET}"
else
    echo -e "${RED}❌ ERROR: The .app bundle was not found at $APP_PATH${RESET}"
    exit 1
fi

echo ""
echo -e "${CYAN}================================================================${RESET}"
echo -e "${GREEN}${BOLD}✨ Build & Deployment Complete! Enjoy TP2ICU v${APP_VERSION}.${RESET}"
echo -e "${CYAN}================================================================${RESET}"
