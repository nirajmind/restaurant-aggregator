package com.niraj.restaurant.config;

import com.niraj.restaurant.domain.ERole;
import com.niraj.restaurant.domain.Role;
import com.niraj.restaurant.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        log.info("STARTING: Role Initialization...");

        Arrays.stream(ERole.values()).forEach(roleName -> {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder().name(roleName).build();
                roleRepository.save(role);
                log.info("INSERTED: Role {}", roleName);
            } else {
                log.info("SKIPPED: Role {} already exists", roleName);
            }
        });

        log.info("COMPLETED: Role Initialization");
    }
}
