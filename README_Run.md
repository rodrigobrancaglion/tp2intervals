# Running Third Party to Intervals.icu (tp2intervals)

This project integrates third-party workout data with [Intervals.icu](https://intervals.icu), featuring a Kotlin/Spring Boot backend and an Angular frontend.

---

## 🛠 Local Development (Without Docker)

### 1. Backend (Spring Boot - Java 21)

- **Via CLI (Command Line):**
  ```bash
  cd boot
  ./gradlew bootRun --args='--spring.profiles.active=dev'
  ```
  *(Or simply `./gradlew bootRun`)*

- **Via IntelliJ IDEA / IDE:**
  - Run configuration: **`boot`** (or create a Spring Boot configuration with):
    - **Main class:** `org.freekode.tp2intervals.Application`
    - **Active profiles:** `dev`
    - **JDK:** Java 21
  - **API available at:** `http://localhost:8080`

### 2. Frontend (Angular)

- **Via CLI (Command Line):**
  ```bash
  cd ui
  npm install
  npm run dev
  ```
  *(Or `ng serve`)*

- **Via IntelliJ IDEA / IDE:**
  - Run configuration: **`ui`** (npm script `dev`)
  - **View at:** `http://localhost:4200` (with live reload proxying API calls to backend)

### 3. Run Both Concurrently in IntelliJ IDEA

- Execute the Compound Run Configuration: **`Application`**
  - This simultaneously launches both the **`boot`** backend and **`ui`** frontend tasks.

---

## 💻 Docker Execution Modes

### 1. Development Mode (Separate Containers)
Best for containerized development with hot-reloading for the frontend.

```bash
docker-compose up --build
```
- **Backend:** `http://localhost:8080`
- **Frontend:** `http://localhost:4200` (with live-reload)
- **Containers:** `tp2intervals-backend` and `tp2intervals-frontend`

### 2. Production / Cloud Mode (Unified Single Container)
Packages the compiled Angular frontend directly inside the Spring Boot JAR. This matches the Google Cloud Run deployment setup.

```bash
# Build the unified image
docker build -t tp2intervals .

# Run the container
docker run -d --name tp2intervals-app -p 8080:8080 tp2intervals
```
- **Unified URL:** `http://localhost:8080` (Spring Boot serves both API endpoints and the Angular static assets)

---

## 🖥 Desktop App (Electron)

To build and package the native desktop application (macOS):

```bash
./createApp.sh
```
- **Executable path:** `electron/dist/mac-arm64/tp2intervals.app`
- *The script also automatically copies the app to `/Applications/tp2intervals.app`.*

---

## ☁️ Google Cloud Run Deployment

Deployment is automated via GitHub Actions on push to the `main` branch.
- GitHub Actions builds the unified image via `Dockerfile` and deploys a single container service to Google Cloud Run.