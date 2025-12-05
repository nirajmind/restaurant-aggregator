package com.niraj.restaurant.repository;

import com.niraj.restaurant.domain.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {
    Optional<User> findByUsername(@NonNull String username);
}
