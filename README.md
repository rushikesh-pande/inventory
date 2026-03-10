# inventory — Enhancement #2

## Overview
Inventory Management Integration microservice — prevents selling out-of-stock items
and automatically tracks stock levels across the order lifecycle.

## Features
- ✅ Check product availability before order creation
- ✅ Reserve inventory when order is created (30-min hold)
- ✅ Auto-release reserved stock on order cancellation
- ✅ Auto-deduct stock after payment confirmation
- ✅ Low stock alerts via Kafka
- ✅ REST API for inventory CRUD and stock management

## Kafka Topics
| Topic | Type | Description |
|-------|------|-------------|
| `inventory.reserved` | Produces | Stock reserved for order |
| `inventory.updated` | Produces | Stock level changed |
| `inventory.low.stock` | Produces | Low stock alert |
| `inventory.reservation.released` | Produces | Reservation released |
| `order.created` | Consumes | Confirm reservation |
| `order.cancelled` | Consumes | Release reservation |
| `payment.completed` | Consumes | Final deduction |

## API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| POST | /api/v1/inventory | Add product |
| GET  | /api/v1/inventory | Get all inventory |
| GET  | /api/v1/inventory/{productId} | Get product inventory |
| GET  | /api/v1/inventory/{productId}/available?qty=N | Check availability |
| GET  | /api/v1/inventory/low-stock | Get low-stock items |
| POST | /api/v1/inventory/reserve | Reserve stock for order |
| PUT  | /api/v1/inventory/stock | Update stock levels |

## Port: 8086

## 🔒 Security Enhancements

This service implements all 7 security enhancements:

| # | Enhancement | Implementation |
|---|-------------|----------------|
| 1 | **OAuth 2.0 / JWT** | `SecurityConfig.java` — stateless JWT auth, Bearer token validation |
| 2 | **API Rate Limiting** | `RateLimitingFilter.java` — 100 req/min per IP using Bucket4j |
| 3 | **Input Validation** | `InputSanitizer.java` — SQL injection, XSS, command injection prevention |
| 4 | **Data Encryption** | `EncryptionService.java` — AES-256-GCM for sensitive data at rest |
| 5 | **PCI DSS** | `PciDssAuditAspect.java` — Full audit trail for payment operations |
| 6 | **GDPR Compliance** | `GdprDataService.java` — Right to erasure, consent management, data export |
| 7 | **Audit Logging** | `AuditLogService.java` — All transactions logged with user, IP, timestamp |

### Security Endpoints
- `GET /api/v1/audit/recent?limit=100` — Recent audit events (ADMIN only)
- `GET /api/v1/audit/user/{userId}` — User's audit trail (ADMIN or self)
- `GET /api/v1/audit/violations` — Security violations (ADMIN only)

### JWT Authentication
```bash
# Include Bearer token in all requests:
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8086/api/v1/...
```

### Security Headers Added
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`
- `Referrer-Policy: strict-origin-when-cross-origin`
- `X-RateLimit-Remaining: <n>` (on every response)
