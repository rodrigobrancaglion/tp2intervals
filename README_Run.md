# Third Party to Intervals.icu

Este projeto integra dados de terceiros com o Intervals.icu, possuindo um backend em Spring Boot (Java) e um frontend em Angular.

## 🛠 Desenvolvimento Local (Sem Docker)
- **Backend (Intellij)**
    - Execute a Run Configuration 'Application'.
    - API disponível em: http://localhost:8080

- **Frontend (Angular CLI)**
    - cd ui && npm install && ng serve
    - **View:** http://localhost:4200


## 💻 Modos de Execução (Docker)
Existem duas formas de rodar o projeto com Docker, dependendo da sua necessidade:

### 1. Modo Desenvolvimento (Containers Separados)
   Ideal para quando você está programando. Usa o Docker Compose para criar dois containers distintos, permitindo ver as alterações no frontend em tempo real.

- Comando:
```shell
docker-compose up --build
```
- **Backend:** http://localhost:8080
- **Frontend:** http://localhost:4200 (com live-reload)
- **Destaque:** No Docker Desktop, você verá dois containers: tp2intervals-backend e tp2intervals-frontend.

### 2. Modo Produção / Cloud (Unificado)
   Este modo utiliza o Dockerfile para "soldar" o Frontend dentro do Backend. É o modo obrigatório para o deploy no Google Cloud Run e para testes de performance local.

- Build & Run:
```shell
# Constrói a imagem unificada
docker build -t tp2intervals .

# Roda o container com nome personalizado
docker run -d --name tp2intervals-app -p 8080:8080 tp2intervals
```
- URL Única: http://localhost:8080 (O Java serve tanto a API quanto o site Angular)

## 🖥 Desktop (App Electron)
Para gerar e rodar a versão instalável do aplicativo:
1. Build do App:

```shell
./app_rebuild_electron.sh
```
2. Executável: Localize o arquivo em: electron/dist/mac-arm64/tp2intervals.app

## ☁️ Deploy no Google Cloud Run
O deploy é automatizado via GitHub Actions sempre que um push é feito para a branch main.
- **Nota:** O GitHub Actions utiliza o Modo Unificado (Dockerfile), gerando um único serviço no Cloud Run para otimizar custos e performance.

---