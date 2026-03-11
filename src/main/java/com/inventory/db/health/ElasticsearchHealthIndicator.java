package com.inventory.db.health;

import org.springframework.boot.actuate.health.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

/**
 * Database Optimisation Enhancement: Elasticsearch Health Indicator
 */
@Component("elasticsearchHealth")
public class ElasticsearchHealthIndicator implements HealthIndicator {

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ElasticsearchHealthIndicator(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Health health() {
        try {
            boolean up = elasticsearchTemplate.indexOps(
                    org.springframework.data.elasticsearch.core.IndexCoordinates.of("inventory-index"))
                    .exists();
            return Health.up()
                    .withDetail("service",       "inventory")
                    .withDetail("elasticsearch", "reachable")
                    .withDetail("index",         "inventory-index (exists=" + up + ")")
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("service",       "inventory")
                    .withDetail("elasticsearch", "unreachable")
                    .withDetail("error",         ex.getMessage())
                    .build();
        }
    }
}
