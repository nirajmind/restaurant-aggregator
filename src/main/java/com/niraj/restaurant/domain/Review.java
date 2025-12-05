package com.niraj.restaurant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(nullable = false) private Short rating;
    private String content;
    @Column(length = 128) private String author;
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
}

