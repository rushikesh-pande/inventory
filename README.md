# InventoryService — Enhancement #2 (NEW)

**Inventory Management Integration** — Spring Boot 3.2.2, Java 17, Kafka

## Features
- ✅ Check product availability before order creation
- ✅ Reserve inventory atomically (30-min TTL)
- ✅ Auto-update stock levels after payment
- ✅ Low stock alerts via Kafka
- ✅ Daily scheduler expires stale reservations

## Kafka Topics
| Topic | Event |
|-------|-------|
| `inventory.reserved`  | Inventory successfully reserved for an order |
| `inventory.updated`   | Stock level changed |
| `inventory.low.stock` | Product hits low-stock threshold |

## API
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/inventory/check-and-reserve` | Check + reserve stock |
| PUT  | `/api/inventory/stock`             | Update stock level    |
| GET  | `/api/inventory/low-stock`         | Get low-stock items   |
| GET  | `/api/inventory/health`            | Health check          |

## Run
```bash
mvn spring-boot:run
```
