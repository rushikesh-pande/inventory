package com.inventory.controller;

import com.inventory.dto.*;
import com.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    /** POST /api/v1/inventory — Add new product to inventory */
    @PostMapping
    public ResponseEntity<InventoryResponse> addProduct(@Valid @RequestBody InventoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.addProduct(req));
    }

    /** GET /api/v1/inventory/{productId} — Get inventory for a product */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    /** GET /api/v1/inventory — Get all inventory */
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    /** GET /api/v1/inventory/low-stock — Get all low-stock items */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    /** GET /api/v1/inventory/{productId}/available?qty=5 — Check availability */
    @GetMapping("/{productId}/available")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable String productId,
            @RequestParam(defaultValue = "1") int qty) {
        return ResponseEntity.ok(inventoryService.isAvailable(productId, qty));
    }

    /** POST /api/v1/inventory/reserve — Reserve stock for an order */
    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(@Valid @RequestBody ReserveRequest req) {
        return ResponseEntity.ok(inventoryService.reserveStock(req));
    }

    /** PUT /api/v1/inventory/stock — Update stock levels */
    @PutMapping("/stock")
    public ResponseEntity<InventoryResponse> updateStock(@RequestBody StockUpdateRequest req) {
        return ResponseEntity.ok(inventoryService.updateStock(req));
    }
}
