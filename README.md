# PayJor

A lightweight backend wallet service built with Spring Boot that provides user authentication (Google OAuth), JWT and API key authentication, Paystack deposit integration, wallet management (balance, transactions), and peer-to-peer transfers.

**Why this project is useful**
- **Simple wallet API**: deposit via Paystack, transfer between users, and view transactions and balances.
- **Flexible auth**: supports Google OAuth -> JWT for users and first-class API keys for non-interactive access.
- **Extensible**: built with Spring Boot, JPA, and modular services — easy to extend and test.

**Quick links**
- **Swagger UI**: `/swagger-ui/index.html`
- **OpenAPI JSON**: `/v3/api-docs`
- **Main config**: `src/main/resources/application.properties`

**Requirements**
- Java 17
- Maven (project includes the Maven wrapper)
- PostgreSQL (or configure another JDBC datasource)

**Environment variables**
See `src/main/resources/application.properties` for the configuration keys. Common variables you must set (examples):

- `GOOGLE_CLIENT_ID` — Google OAuth client id
- `GOOGLE_SECRET` — Google OAuth client secret
- `GOOGLE_SCOPE` — OAuth scopes (e.g. `openid,email,profile`)
- `GOOGLE_BASE_URI` — OAuth redirect URI (should match Google console entry)
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` — JDBC datasource
- `RSA_PRIVATE_KEY`, `RSA_PUBLIC_KEY` — RSA PEM keys used to sign/verify JWTs
- `PAYSTACK_SECRET`, `PAYSTACK_PUBLIC`, `PAYSTACK_BASE_URL` — Paystack credentials

**Build & Run**
From Windows PowerShell at the repo root:

```powershell
.\\mvnw.cmd -DskipTests package
java -jar target\payjor-0.0.1-SNAPSHOT.jar
```

Or run with the Maven wrapper directly for development:

```powershell
.\\mvnw.cmd spring-boot:run
```

**Authentication**
- JWT: Obtained from Google OAuth flow. The app uses RSA keys to issue JWTs. Send as header: `Authorization: Bearer <token>`.
- API Key: Create an API key in the app and use header `X-API-KEY: <your_plain_api_key>`.

Roles/permissions (used as `ROLE_<NAME>` by the security filters):
- `READ` — access balance and transactions
- `TRANSFER` — perform transfers
- `DEPOSIT` — initiate deposits

**Key API endpoints and examples**

Base URL: `http://localhost:8080` (or your deployment URL)

- **Initiate Google sign-in**

  - GET `/auth/google`
  - Response: `{ "authorization_url": "https://accounts.google.com/..?" }`

  Example:

  ```bash
  curl -v http://localhost:8080/auth/google
  ```

- **Complete Google sign-in (exchange code for JWT)**

  - GET `/auth/google/callback?code=<code>`
  - Response: `{ "data": { "token": "<jwt>" } }`

  Example:

  ```bash
  curl "http://localhost:8080/auth/google/callback?code=CODE_FROM_GOOGLE"
  ```

- **Create API Key**

  - POST `/keys/create`
  - Auth: must be authenticated (JWT or another API key with appropriate permissions)
  - Body (JSON):

    ```json
    {
      "name": "my-key",
      "permissions": ["READ","TRANSFER"],
      "expiry": "1M"
    }
    ```

  - Response: `{ "data": { "apiKey": "sk_live_...", "expiresAt": "..." } }`

  Example:

  ```bash
  curl -X POST http://localhost:8080/keys/create \
    -H "Authorization: Bearer $JWT" \
    -H "Content-Type: application/json" \
    -d '{"name":"my-key","permissions":["READ"],"expiry":"1Y"}'
  ```

- **Deposit (initialize Paystack payment)**

  - POST `/wallet/deposit` (body is an integer amount in the request body in the current implementation)
  - Requires `DEPOSIT` role
  - Response includes `reference` and `authorizationUrl` returned from Paystack

  Example:

  ```bash
  curl -X POST http://localhost:8080/wallet/deposit \
    -H "Authorization: Bearer $JWT" \
    -H "Content-Type: application/json" \
    -d 5000
  ```

- **Verify deposit status**

  - GET `/wallet/deposit/{reference}/status`

  Example:

  ```bash
  curl http://localhost:8080/wallet/deposit/REF123/status -H "Authorization: Bearer $JWT"
  ```

- **Handle Paystack webhook**

  - POST `/wallet/paystack/webhook`
  - Header: `X-Paystack-Signature` (signature computed with `paystack.secret-key`)
  - Body: full webhook JSON payload from Paystack

  The service validates HMAC-SHA512 of the payload against `PAYSTACK_SECRET`.

- **Get balance**

  - GET `/wallet/balance`
  - Requires `READ` role

  Example:

  ```bash
  curl http://localhost:8080/wallet/balance -H "Authorization: Bearer $JWT"
  ```

- **Transfer between users**

  - POST `/wallet/transfer`
  - Requires `TRANSFER` role
  - Body JSON:

    ```json
    { "walletNumber": "12345678", "amount": 100.0 }
    ```

  Example:

  ```bash
  curl -X POST http://localhost:8080/wallet/transfer \
    -H "Authorization: Bearer $JWT" \
    -H "Content-Type: application/json" \
    -d '{"walletNumber":"123","amount":10.5}'
  ```

- **Get transactions**

  - GET `/wallet/transactions`
  - Requires `READ` role

  Example:

  ```bash
  curl http://localhost:8080/wallet/transactions -H "Authorization: Bearer $JWT"
  ```

**Project structure (high level)**
- `src/main/java/com/bituan/payjor` — main source
  - `config` — security, JWT, API key filters
  - `controller` — REST controllers (`AuthController`, `WalletController`, `ApiKeyController`)
  - `service` — business logic (`wallet`, `auth`, `paystack`, `user` packages)
  - `model` — request/response DTOs, JPA entities, enums
- `src/main/resources` — properties and certs

**Testing**
- Unit tests use the Spring Boot test starter. Run tests with:

```powershell
.\\mvnw.cmd test
```

**Where to get help**
- Check `README.md` and `src/main/resources/application.properties`.
- Open an issue in the repository for bugs or feature requests.

**Maintainers & contributing**
- Maintained by the repository owner; see repository metadata for details.
- For contribution guidelines, please add a `CONTRIBUTING.md` or follow standard GitHub PR workflow (fork → branch → PR).

---
