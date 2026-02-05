package com.example.FinCiclo.config;

import com.example.FinCiclo.entity.Role;
import com.example.FinCiclo.enums.RoleName;
import com.example.FinCiclo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        log.info("Inicializando roles...");

        // Verificar e insertar ROLE_USER
        if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
            Role userRole = new Role(RoleName.ROLE_USER);
            roleRepository.save(userRole);
            log.info("✅ Rol ROLE_USER creado");
        }

        // Verificar e insertar ROLE_PROGRAMADOR
        if (!roleRepository.existsByName(RoleName.ROLE_PROGRAMMER)) {
            Role programadorRole = new Role(RoleName.ROLE_PROGRAMMER);
            roleRepository.save(programadorRole);
            log.info("✅ Rol ROLE_PROGRAMADOR creado");
        }

        // Verificar e insertar ROLE_ADMIN
        if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {
            Role adminRole = new Role(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
            log.info("✅ Rol ROLE_ADMIN creado");
        }

        log.info("✅ Inicialización de roles completada");
    }
}