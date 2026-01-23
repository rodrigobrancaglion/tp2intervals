# --- ETAPA 1: Build do Frontend (Angular) ---
FROM node:20-alpine AS frontend-builder
WORKDIR /app
COPY ui/package*.json ./
RUN npm install
COPY ui/ .
RUN npm run build -- --configuration production

# --- ETAPA 2: Build do Backend (Java) ---
FROM amazoncorretto:21-alpine AS builder
WORKDIR /build-app
ARG JAR_PATH
COPY . .

# Move o Angular para dentro do Java antes do build
COPY --from=frontend-builder /app/dist/ui/browser/ boot/src/main/resources/static/

RUN if [ -z "$JAR_PATH" ]; then \
        apk add --no-cache findutils dos2unix && \
        cd boot && \
        dos2unix gradlew && chmod +x gradlew && \
        ./gradlew :bootJar --no-daemon && \
        find build/libs/ -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app.jar \; ; \
    else \
        cp "$JAR_PATH" /app.jar; \
    fi

# --- ETAPA 3: Imagem Final ---
FROM amazoncorretto:21-alpine
WORKDIR /app

RUN mkdir -p /app/data

COPY --from=builder /app.jar app.jar

COPY tp2intervals.sqlite /app/data/tp2intervals.sqlite

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080} --spring.datasource.url=jdbc:sqlite:/app/data/tp2intervals.sqlite"]