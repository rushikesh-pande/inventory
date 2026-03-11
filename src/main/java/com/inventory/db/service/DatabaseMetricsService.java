package com.inventory.db.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Database Optimisation Enhancement: Database Metrics Service
 *
 * Tracks cache and query performance metrics for inventory.
 * Exposed to Prometheus via /actuator/prometheus.
 *
 * Metrics:
 *  - inventory_cache_hits_total       — Redis cache hits
 *  - inventory_cache_misses_total     — Redis cache misses (DB queries)
 *  - inventory_db_queries_total       — Total DB queries by type
 *  - inventory_db_slow_queries_total  — Queries above 500ms
 *  - inventory_connection_pool_active — HikariCP active connections
 */
@Service
public class DatabaseMetricsService {

    private final MeterRegistry meterRegistry;
    private final AtomicLong activeConnections = new AtomicLong(0);

    public DatabaseMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("inventory.connection.pool.active", activeConnections, AtomicLong::get)
             .description("Active HikariCP connections for inventory")
             .tag("service", "inventory")
             .register(meterRegistry);
    }

    public void recordCacheHit(String cacheName) {
        Counter.builder("inventory.cache.hits.total")
               .tag("service", "inventory").tag("cache", cacheName)
               .description("Redis cache hits for inventory")
               .register(meterRegistry).increment();
    }

    public void recordCacheMiss(String cacheName) {
        Counter.builder("inventory.cache.misses.total")
               .tag("service", "inventory").tag("cache", cacheName)
               .description("Redis cache misses for inventory (DB fallback)")
               .register(meterRegistry).increment();
    }

    public void recordDbQuery(String queryType) {
        Counter.builder("inventory.db.queries.total")
               .tag("service", "inventory").tag("type", queryType)
               .description("DB queries for inventory")
               .register(meterRegistry).increment();
    }

    public void recordSlowQuery(String queryType, long ms) {
        Counter.builder("inventory.db.slow.queries.total")
               .tag("service", "inventory").tag("type", queryType)
               .description("DB queries exceeding 500ms for inventory")
               .register(meterRegistry).increment();
        meterRegistry.summary("inventory.db.query.duration",
                "service", "inventory", "type", queryType).record(ms);
    }

    public void setActiveConnections(long count) {
        activeConnections.set(count);
    }
}
