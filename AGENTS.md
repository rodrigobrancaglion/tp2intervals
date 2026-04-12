# AGENTS.md

## Project Structure

- `boot/` - Kotlin/Spring Boot backend (JDK 21)
- `ui/` - Angular 17 frontend
- `electron/` - Electron desktop app wrapper
- `cypress/` - End-to-end tests

## Build Commands

### Backend
```bash
cd boot && ./gradlew build      # Build JAR + run tests
cd boot && ./gradlew test       # Run unit tests only
cd boot && ./gradlew jar         # Build JAR without tests
```

### Frontend
```bash
cd ui && npm install && npm run build
```

### Electron App
```bash
cd ui && npm run build --prefix ui -- --base-href=./   # Build UI first
cd electron && npm start                               # Dev mode
```

### End-to-End Tests
```bash
java -jar boot/build/libs/tp2intervals.jar &   # Start app first
npm test --prefix cypress
```

## Key Facts

- JAR output: `boot/build/libs/tp2intervals.jar`
- UI is built to `ui/dist/ui/browser` and copied to `boot/src/main/resources/static` during CI
- Version: read from `boot/version`
- Backend tests use WireMock (files in `config/mock/`)
- Integration tests named `*IT.kt`
- Frontend uses SCSS, Angular Material, proxy config at `ui/src/proxy.conf.json`
