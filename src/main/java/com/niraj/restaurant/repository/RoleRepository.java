package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.ERole;
import com.niraj.restaurant.domain.Role;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<@NonNull Role, @NonNull Integer> {
    Optional<Role> findByName(ERole name);
}
