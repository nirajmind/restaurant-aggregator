package com.niraj.restaurant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "source")
@Getter
@Setter
@NoArgsConstructor
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 64) private String name;
    @Column(name = "base_url") private String baseUrl;
    @Column(nullable = false) private Boolean enabled = true;
}

