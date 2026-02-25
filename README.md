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
