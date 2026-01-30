# Platform Auth Service

A production-ready authentication and authorization service built with Spring Boot.
This service provides JWT-based authentication, refresh token rotation, Redis-backed rate limiting,
full audit logging, and deep observability (metrics, logs, traces).

---

## 🚀 Features

- JWT authentication (access + refresh tokens)
- Secure refresh token rotation & revocation
- Stateless Spring Security configuration
- Redis-backed rate limiting (Bucket4j)
- Authentication audit logging (Postgres)
- Request ID + Trace ID propagation
- Prometheus metrics
- Grafana dashboards
- Loki structured logs
- Tempo distributed tracing
- Docker-first deployment

---

## 🧱 Tech Stack

- Java 21
- Spring Boot
- Spring Security
- PostgreSQL 16
- Redis 7
- Bucket4j
- Micrometer
- Prometheus / Grafana
- Loki / Promtail
- Tempo
- Docker

---

## 📦 API Endpoints

### Auth
| Method | Path | Description |
|------|------|-------------|
| POST | `/v1/auth/register` | Register new user |
| POST | `/v1/auth/login` | Login |
| POST | `/v1/auth/refresh` | Refresh tokens |
| POST | `/v1/auth/logout` | Logout & revoke refresh token |

### User
| Method | Path | Description |
|------|------|-------------|
| GET | `/v1/me` | Get current user |

### Health & Metrics
| Method | Path |
|------|------|
| GET | `/v1/health` |
| GET | `/actuator/health` |
| GET | `/actuator/prometheus` |

---

## 🔐 Security Model

- Stateless JWT authentication
- Access tokens contain user ID (UUID)
- Refresh tokens stored server-side and rotated on use
- Logout revokes refresh tokens
- Custom JSON error responses
- No sessions, no cookies

---

## ⏱ Rate Limiting

Implemented using **Bucket4j + Redis**:

- Per-IP limits for unauthenticated endpoints
- Per-user limits for authenticated endpoints
- Redis-backed to support horizontal scaling
- Enforced via servlet filter

---

## 🧾 Auth Audit Logging

All authentication-related events are persisted to PostgreSQL:

- LOGIN_SUCCESS
- LOGIN_FAILURE
- REGISTER_SUCCESS
- REGISTER_FAILURE
- REFRESH_SUCCESS / FAILURE
- LOGOUT

Captured metadata:
- timestamp
- email
- outcome
- request path
- request ID
- trace ID
- failure reason (if any)

---

## 📊 Observability

### Metrics
- Request latency
- Status codes
- Rate limit hits
- JVM metrics

### Logs
- Structured logs
- Correlated by request ID + trace ID
- Ingested by Loki

### Traces
- End-to-end request tracing
- Exported to Tempo
- Visualized in Grafana

---

## 🐳 Running Locally

This service is designed to run via Docker Compose from **platform-infra**.

bash
docker compose up -d platform-auth-service.

---

### 🧪 Smoke Test

A full smoke test validates:

- Registration
- Login
- /v1/me
- Token refresh
- Logout
- Refresh token revocation

All tests must return HTTP 200 except revoked refresh (401).

--- 

## 📌 Status

✅ Authentication complete
✅ Rate limiting enforced
✅ Audit logs persisted
✅ Metrics, logs, traces working

---
