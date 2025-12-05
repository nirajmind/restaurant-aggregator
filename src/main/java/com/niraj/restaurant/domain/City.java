package com.niraj.restaurant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "city", uniqueConstraints = @UniqueConstraint(columnNames = {"name","country"}))
@Getter
@Setter
@NoArgsConstructor
public class City {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 128) private String name;
    @Column(nullable = false, length = 64) private String country;
    @Column(nullable = false, length = 64) private String tz = "Asia/Dubai";
}

