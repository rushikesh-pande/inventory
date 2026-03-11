package com.inventory.db.search;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Database Optimisation Enhancement: Elasticsearch Search Repository
 * Provides full-text search over inventory entities.
 */
@Repository
public interface InventorySearchRepository
        extends ElasticsearchRepository<InventorySearchDocument, String> {

    List<InventorySearchDocument> findByNameContainingOrDescriptionContaining(
            String name, String description);

    List<InventorySearchDocument> findByStatus(String status);

    List<InventorySearchDocument> findByCategory(String category);

    @Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"name^2\",\"description\",\"category\"],\"fuzziness\":\"AUTO\",\"type\":\"best_fields\"}}")
    List<InventorySearchDocument> fuzzySearch(String query);
}
