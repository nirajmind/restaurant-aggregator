package com.niraj.restaurant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity @Table(name = "restaurant_source_map",
        uniqueConstraints = @UniqueConstraint(columnNames = {"source_id","external_id"}))
@Getter
@Setter
@NoArgsConstructor
public class RestaurantSourceMap {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "source_id")
    private Source source;

    @Column(name = "external_id", nullable = false, length = 128)
    private String externalId;

    @Column(name = "external_slug", length = 256)
    private String externalSlug;

    @Column(name = "raw_hash", length = 64)
    private String rawHash;

    public RestaurantSourceMap(Restaurant r, Source s, String externalId, String externalSlug, String rawHash) {
        this.restaurant = r; this.source = s; this.externalId = externalId; this.externalSlug = externalSlug; this.rawHash = rawHash;
    }
}

