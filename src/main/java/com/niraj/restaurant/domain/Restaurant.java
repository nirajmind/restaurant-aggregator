package com.niraj.restaurant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "canonical_name", nullable = false) private String canonicalName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "city_id") private City city;
    private Short priceRange;
    private BigDecimal avgRating;
    private String phone;
    private String website;
    private Boolean isOpen;

    @ManyToMany
    @JoinTable(
            name = "restaurant_cuisine",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisine_id")
    )
    private Set<Cuisine> cuisines = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "restaurant_tag",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menuItems = new ArrayList<>();

}

