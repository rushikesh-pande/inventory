package com.inventory.db.search;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

/**
 * Database Optimisation Enhancement: Elasticsearch Search Document
 *
 * Maps inventory domain entity to an Elasticsearch index.
 * Used for full-text search queries that are too expensive for SQL LIKE.
 *
 * Sync strategy: Kafka consumer updates the ES index on entity changes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "inventory-index")
@Setting(shards = 3, replicas = 1)
public class InventorySearchDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Double)
    private Double amount;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private String createdAt;

    @Field(type = FieldType.Keyword)
    private String serviceSource;   // which microservice created this record
}
