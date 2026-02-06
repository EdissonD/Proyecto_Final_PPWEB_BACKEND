package com.marcelo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcelo.backend.models.Programador;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramadorRepository extends JpaRepository<Programador, UUID> {
    // Busca un programador basándose en el ID de su Usuario (Login)
    Optional<Programador> findByUsuarioId(UUID usuarioId);
}