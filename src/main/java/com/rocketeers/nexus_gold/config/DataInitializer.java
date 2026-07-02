package com.rocketeers.nexus_gold.config;

import com.rocketeers.nexus_gold.model.Role;
import com.rocketeers.nexus_gold.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("ADMIN").build());
            roleRepository.save(Role.builder().name("SELLER").build());
            roleRepository.save(Role.builder().name("USER").build());
        }
    }
}
