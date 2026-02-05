package com.example.FinCiclo.repository;

import com.example.FinCiclo.entity.Role;
import com.example.FinCiclo.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
