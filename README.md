# API Versioning

This project demonstrates **API versioning with path-based prefixes** in a Spring Boot application.
It uses a custom `@Version` annotation and a custom `RequestMappingHandlerMapping` to automatically add version prefixes to your endpoints.

---

## 📂 Project Structure

* `annotations/Version.java`
  Custom annotation to declare API version on controllers or methods.

* `handlers/VersionRequestMappingHandler.java`
  Custom `RequestMappingHandlerMapping` that applies the versioning logic.

* `configs/WebConfig.java`
  Replaces Spring Boot’s default handler mapping with our custom version-aware handler.

* `controllers/TestVersionController.java`
  Example controller showing how to use `@Version`.

---

## ⚙️ How Versioning Works

* Class-level `@Version("v1")` → all endpoints default under `/api/v1/...`
* Method-level `@Version("v2")` → overrides class-level
* Method-level `@Version()` (blank) → explicitly unversioned (only `/api/...`)
* No `@Version` at all → unversioned (only `/api/...`)

The base API path is configurable in `application.yml`:

```yaml
api:
  base-path: /api
```

---

## ▶️ Running the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/<your-username>/api-versioning.git
   cd api-versioning
   ```

2. Build and run with Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

   or with Gradle:

   ```bash
   ./gradlew bootRun
   ```

3. The app will start on [http://localhost:8080](http://localhost:8080).

### 🔀 Changing the Port

If port **8080** is already in use, you can change the port in `application.yml`:

```yaml
server:
  port: 9090
```

Then access the application at:

```
http://localhost:9090
```

---

## 🧪 Testing the Endpoints

Example controller: `TestVersionController`

### Endpoints

* **Class-level versioned (v1)**

  ```
  GET /api/v1/test/hello
  → "Hello from v1 (class-level)"

  GET /api/v1/test/ping
  → "Ping v1 OK"
  ```

* **Method-level override (v2)**

  ```
  GET /api/v2/test/hello
  → "Hello from v2 (method-level override)"
  ```

* **Explicitly unversioned**

  ```
  GET /api/test/info
  → "Unversioned endpoint"
  ```

### Test with curl

```bash
curl http://localhost:8080/api/v1/test/hello
curl http://localhost:8080/api/v2/test/hello
curl http://localhost:8080/api/test/info
```

(If you changed the port, replace `8080` with the new port.)

---

## ✅ Use Cases

* Support multiple API versions without duplicating controllers.
* Gracefully deprecate old versions while adding new ones.
* Keep backward compatibility for clients.

---

## 📜 Request Flow Diagram

```
Client Request
     |
     v
VersionRequestMappingHandler
     |
     v
 Controller (with @Version)
```

---

## 📜 License

This project is licensed under the MIT License.
