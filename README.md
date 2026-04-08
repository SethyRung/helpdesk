# Helpdesk

Role-based ticket management system with Keycloak OAuth2/OIDC authentication.

## Architecture

Four services orchestrated via Docker Compose:

| Service         | Technology                                | Port |
| --------------- | ----------------------------------------- | ---- |
| PostgreSQL      | 18-alpine                                 | 5432 |
| Keycloak        | 26.5 (custom Vue theme)                   | 8080 |
| Spring Boot API | 4.0.3 / Java 25                           | 8000 |
| Vue 3 Web       | Vite + Vue 3 + Nuxt UI + Pinia + Tailwind | 5173 |

**PostgreSQL** hosts two databases:

- `keycloak` - Keycloak identity provider data
- `helpdesk` - Application data

## Prerequisites

- Docker and Docker Compose
- Java 25+ (for local backend development)
- Node 22+ and pnpm (for local frontend development)

## Quick Start

### 1. Environment Setup

```bash
cp .env.example .env
```

Edit `.env` to configure your environment. Default values work for local development.

### 2. Start All Services

```bash
docker-compose up --build
```

### 3. Access Services

| Service            | URL                                   |
| ------------------ | ------------------------------------- |
| Web App            | http://localhost:5173                 |
| API                | http://localhost:8000                 |
| API Docs (Swagger) | http://localhost:8000/swagger-ui.html |
| Keycloak Admin     | http://localhost:8080/admin           |
| Keycloak Realm     | http://localhost:8080/realms/helpdesk |

### 4. Default Credentials

**Keycloak Admin Console:**

- Username: `admin`
- Password: `admin`

## Keycloak Configuration

### Option A: Import Realm (Recommended)

1. Navigate to Keycloak Admin Console: http://localhost:8080/admin
2. Log in with admin credentials.
3. Hover over the dropdown in the top-left corner (next to "Master")
4. Click **Create realm**
5. Select **Import** and upload realm export JSON file located at `keycloak/realm-export.json`
6. Click **Create**

The realm import will configure:

- Realm name: `helpdesk`
- Client: `helpdesk-client` with proper redirect URIs
- Client roles: `user`, `admin`
- Email verification enabled
- Password reset enabled
- Registration enabled

### Option B: Manual Configuration

If you prefer to configure manually:

#### Create Realm

1. In the admin console, click **Create realm**
2. Name it `helpdesk`
3. Click **Create**

#### Create Client

1. Go to **Clients** → **Create client**
2. Client ID: `helpdesk-client`
3. Client authentication: **Off** (public client)
4. Click **Next**

**Valid redirect URIs:**

```
http://localhost:8000/api/auth/callback
```

**Valid post logout redirect URIs:**

```
http://localhost:5173/*
```

**Web origins:**

```
http://localhost:5173
```

#### Create Client Roles

1. Go to **Clients** → `helpdesk-client` → **Roles** → **Add role**
2. Create roles: `user`, `admin`

#### Create Test Users

1. Go to **Users** → **Add user**
2. Fill in username, email, first name, last name
3. Email verified: **ON**
4. Click **Create**
5. Go to **Credentials** tab
6. Set a password
7. Temporary: **OFF**
8. Go to **Role mapping** tab
9. Assign `user` or `admin` client role

### Environment Variables for Keycloak

Update your `.env` file to match your Keycloak configuration:

```bash
# Keycloak
SPRING_KEYCLOAK_BASE_URL=http://localhost:8080
SPRING_KEYCLOAK_REALM=helpdesk
SPRING_KEYCLOAK_CLIENT_ID=helpdesk-client
SPRING_KEYCLOAK_REDIRECT_URL=http://localhost:8000/api/auth/callback
```

### SMTP Configuration (Email)

For email verification and password reset features to work, configure SMTP in Keycloak:

1. Go to **Realm Settings** → **Email**
2. Fill in the SMTP settings:

| Setting           | Value                    | Description           |
| ----------------- | ------------------------ | --------------------- |
| Host              | `smtp.resend.com`        | SMTP server host      |
| Port              | `465`                    | SMTP port (SSL)       |
| From              | `noreply@yourdomain.com` | Sender email address  |
| From display name | `Helpdesk`               | Sender name           |
| Reply-to          | _(empty)_                | Reply-to address      |
| Reply-to display  | _(empty)_                | Reply-to display name |
| Enable SSL        | **ON**                   | Use SSL connection    |
| Enable StartTLS   | **ON**                   | Use StartTLS          |
| Auth              | `Username/Password`      | Authentication type   |
| Username          | `resend`                 | SMTP username         |
| Password          | `your-api-key`           | SMTP password/API key |

**Alternative SMTP providers:**

- **Resend** (recommended for modern apps) - smtp.resend.com
- **SendGrid** - smtp.sendgrid.net
- **Mailgun** - smtp.mailgun.org
- **AWS SES** - email-smtp.us-east-1.amazonaws.com
- **Gmail** (for testing) - smtp.gmail.com:587 with app-specific password

## Development

### Backend Development

```bash
cd service
./gradlew build          # Build project
./gradlew clean          # Clean build artifacts
./gradlew bootRun        # Run locally (requires DB and env vars)
```

**Requirements for local backend:**

- PostgreSQL running on port 5432 with `helpdesk` database
- Keycloak running on port 8080
- `.env` file configured at project root

### Frontend Development

```bash
cd web
pnpm install             # Install dependencies
pnpm dev                 # Development server (http://localhost:5173)
pnpm build               # Production build (includes type-check)
pnpm type-check          # TypeScript check only
pnpm lint                # Run oxlint
pnpm lint:fix            # Auto-fix lint issues
pnpm fmt                 # Format with oxfmt
pnpm fmt:check           # Check formatting without modifying
```

### Keycloak Theme Development

The Keycloak login pages use a custom Vue 3 + Nuxt UI theme.

**Build theme:**

```bash
cd web
pnpm keycloak            # Build both app + keycloak, then inject theme
```

**Theme files:**

- `web/src/views/keycloak/` - Vue components (LoginView, RegisterView, etc.)
- `keycloak/themes/helpdesk/login/*.ftl` - FreeMarker templates
- `keycloak/inject-theme.js` - Post-build script

**Theme workflow:**

1. Edit Vue components in `web/src/views/keycloak/`
2. Run `pnpm keycloak`
3. Restart Keycloak container to see changes

### Docker Development

```bash
docker-compose up --build            # Start all services
```

## Authentication Flow

```
1. User clicks "Login" in web app
2. Frontend redirects to Keycloak: /realms/helpdesk/protocol/openid-connect/auth
3. User authenticates with Keycloak (login, register, etc.)
4. Keycloak redirects to backend callback with authorization code
5. Backend exchanges code for tokens (access_token, refresh_token)
6. Backend sets HttpOnly cookies and redirects to frontend
7. Frontend includes cookies in subsequent API requests
8. Custom BearerTokenResolver extracts token from cookie
9. CustomJwtAuthenticationConverter extracts roles from JWT realm_access.roles
10. Roles prefixed with ROLE_ (user → ROLE_USER, admin → ROLE_ADMIN)
```

**Public endpoints (no authentication):**

- `/api/ping` - Health check
- `/api/auth/login` - Initiate login flow
- `/api/auth/callback` - OAuth2 callback
- `/api/auth/check-authenticated` - Check authentication status
- `/api/auth/refresh` - Refresh access token
- `/api/auth/logout` - Logout
- Swagger UI (`/swagger-ui.html`)

**Protected endpoints:** All other endpoints require valid JWT with appropriate roles.

## API Endpoints

### Tickets

| Method | Endpoint                 | Auth  | Description                  |
| ------ | ------------------------ | ----- | ---------------------------- |
| GET    | /api/tickets             | ADMIN | List all tickets             |
| GET    | /api/tickets/my          | USER+ | List current user's tickets  |
| GET    | /api/tickets/{id}        | USER+ | Get a ticket by ID           |
| POST   | /api/tickets             | USER+ | Create a ticket              |
| PUT    | /api/tickets/{id}        | ADMIN | Full update of any ticket    |
| PATCH  | /api/tickets/{id}        | USER  | Partial update of own ticket |
| PATCH  | /api/tickets/{id}/status | ADMIN | Update ticket status only    |
| DELETE | /api/tickets/{id}        | ADMIN | Delete a ticket              |

### Comments

| Method | Endpoint                        | Auth  | Description                   |
| ------ | ------------------------------- | ----- | ----------------------------- |
| GET    | /api/comments/ticket/{ticketId} | USER+ | List comments for a ticket    |
| POST   | /api/comments                   | USER+ | Create a comment              |
| PATCH  | /api/comments/{id}              | USER  | Update own comment            |
| DELETE | /api/comments/{id}              | USER+ | Delete own comment (or admin) |

### Ticket Model

| Field       | Type           | Values                              |
| ----------- | -------------- | ----------------------------------- |
| id          | Long           | Auto-generated                      |
| title       | String         | Required, max 255 chars             |
| description | String         | Required, rich text (TipTap)        |
| priority    | TicketPriority | LOW, MEDIUM, HIGH, URGENT           |
| status      | TicketStatus   | OPEN, IN_PROGRESS, RESOLVED, CLOSED |
| createdBy   | String         | Set from JWT subject on creation    |
| createdAt   | LocalDateTime  | Auto-set on creation                |
| updatedAt   | LocalDateTime  | Auto-updated on change              |

## Project Structure

### Backend (`service/`)

```
com.sethy.service
├── ticket/          # Ticket domain
│   ├── entity/      # JPA entities
│   ├── repository/  # Spring Data repositories
│   ├── service/     # Business logic (interface + impl/)
│   ├── controller/  # REST controllers
│   └── dto/         # Request/Response DTOs
├── comment/         # Comment domain
├── auth/            # Authentication service
├── config/          # Security, JWT, CORS, OpenAPI
├── common/          # Shared utilities
└── ping/            # Health check
```

**Key files:**

- `SecurityConfig.java` - JWT validation, cookie resolver, public endpoints
- `CustomJwtAuthenticationConverter.java` - Role extraction from JWT
- `GlobalExceptionHandler.java` - Centralized error handling
- `ApiResponse.java` - Standard API response wrapper

### Frontend (`web/`)

```
src/
├── types/           # TypeScript interfaces
├── services/        # API service classes
├── stores/          # Pinia stores
├── utils/           # Utilities (api client, date, color)
├── router/          # Vue Router config
├── views/           # Vue components
│   └── keycloak/    # Keycloak theme components
└── main*.ts         # Entry points (app + keycloak)
```

**Key files:**

- `utils/api.ts` - Axios client with token refresh interceptor
- `stores/auth.ts` - Authentication state management
- `AppKeycloak.vue` - Keycloak theme root component

## Security

- JWT tokens validated against Keycloak issuer
- Roles extracted from `realm_access.roles` claim in JWT
- Method-level authorization via `@PreAuthorize` annotations
- CORS configured for frontend origins
- HttpOnly cookies for token storage (prevent XSS)
- CSRF protection enabled
- Swagger UI accessible without auth for API exploration

## Common Patterns

### Backend

**Create a new domain:**

1. Create package structure: `entity/`, `repository/`, `service/`, `controller/`, `dto/`
2. Create entity with JPA annotations
3. Create repository extending `JpaRepository`
4. Create service interface and implementation
5. Create controller with `@Operation` and `@PreAuthorize`
6. Return `ApiResponse<T>` wrapper

**Example response:**

```java
return ApiResponse.success(ticketResponse, "Ticket created successfully");
return ApiResponse.error(ApiResponseCode.NOT_FOUND, "Ticket not found");
```

### Frontend

**Create a new feature:**

1. Define types in `src/types/`
2. Add service methods in `src/services/`
3. Use `api` utility from `@/utils/api`
4. Create Vue component following existing patterns
5. Use Nuxt UI components for UI elements

**Example API call:**

```typescript
import { api } from "@/utils/api";
const response = await api.post<TicketResponse>("/tickets", data);
```

## Environment Variables

| Variable                          | Description               | Default                                                        |
| --------------------------------- | ------------------------- | -------------------------------------------------------------- |
| POSTGRES_USER                     | PostgreSQL user           | postgres                                                       |
| POSTGRES_PASSWORD                 | PostgreSQL password       | postgres                                                       |
| KC_HOSTNAME_PORT                  | Keycloak port             | 8080                                                           |
| KC_BOOTSTRAP_ADMIN_USERNAME       | Keycloak admin username   | admin                                                          |
| KC_BOOTSTRAP_ADMIN_PASSWORD       | Keycloak admin password   | admin                                                          |
| SERVER_PORT                       | API port                  | 8000                                                           |
| SPRING_DATASOURCE_URL             | JDBC URL                  | jdbc:postgresql://...                                          |
| SPRING_DATASOURCE_USERNAME        | Database user             | helpdesk                                                       |
| SPRING_DATASOURCE_PASSWORD        | Database password         | helpdesk                                                       |
| SPRING_WEB_BASE_URL               | Frontend URL              | http://localhost:5173                                          |
| SPRING_CORS_ALLOWED_ORIGINS       | CORS allowed origins      | localhost:5173,8080                                            |
| SPRING_KEYCLOAK_BASE_URL          | Public Keycloak URL       | http://localhost:8080                                          |
| SPRING_KEYCLOAK_INTERNAL_BASE_URL | Internal Keycloak URL     | (local) http://localhost:8080 or (docker) http://keycloak:8080 |
| SPRING_KEYCLOAK_REALM             | Keycloak realm name       | helpdesk                                                       |
| SPRING_KEYCLOAK_CLIENT_ID         | Keycloak client ID        | helpdesk-client                                                |
| SPRING_KEYCLOAK_REDIRECT_URL      | OAuth2 callback URL       | http://localhost:...                                           |
| VITE_API_BASE_URL                 | API base URL for frontend | http://localhost:8000                                          |

## Troubleshooting

### Backend fails to start

1. Check PostgreSQL is running: `docker ps`
2. Check database exists: `docker exec -it auth-postgres psql -U postgres -l`
3. Check environment variables in `.env`

### Keycloak login fails

1. Check Keycloak is running: `docker ps`
2. Verify realm name matches `SPRING_KEYCLOAK_REALM`
3. Verify client ID matches `SPRING_KEYCLOAK_CLIENT_ID`
4. Check redirect URI in Keycloak client configuration

### Frontend cannot connect to API

1. Check API is running: `curl http://localhost:8000/api/ping`
2. Verify `VITE_API_BASE_URL` in `.env`
3. Check CORS configuration: `SPRING_CORS_ALLOWED_ORIGINS`
4. If running in Docker, confirm the API container has `SPRING_KEYCLOAK_INTERNAL_BASE_URL=http://keycloak:8080` and `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/auth_platform`

### Keycloak theme not showing

1. Build theme: `cd web && pnpm keycloak`
2. Check files exist: `ls keycloak/themes/helpdesk/login/`
3. Restart Keycloak container: `docker-compose restart keycloak`

## License

[MIT](LICENSE)
