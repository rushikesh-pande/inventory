package com.inventory.controller;
import com.inventory.dto.*;
import com.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/inventory") @RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;
    @PostMapping("/check-and-reserve")
    public ResponseEntity<CheckAndReserveResponse> checkAndReserve(@Valid @RequestBody CheckAndReserveRequest req) {
        return ResponseEntity.ok(service.checkAndReserve(req));
    }
    @PutMapping("/stock")
    public ResponseEntity<Void> updateStock(@Valid @RequestBody StockUpdateRequest req) {
        service.updateStock(req); return ResponseEntity.ok().build();
    }
    @GetMapping("/low-stock")
    public ResponseEntity<List<?>> lowStock() { return ResponseEntity.ok(service.getLowStockItems()); }
    @GetMapping("/health")
    public ResponseEntity<String> health() { return ResponseEntity.ok("InventoryService UP"); }
}
