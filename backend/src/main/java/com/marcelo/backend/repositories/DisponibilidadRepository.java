package com.marcelo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marcelo.backend.models.Disponibilidad;
import com.marcelo.backend.models.Programador;

import java.util.List;
import java.util.UUID;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, UUID> {

    List<Disponibilidad> findByProgramador(Programador programador);

    List<Disponibilidad> findByProgramadorId(UUID programadorId);

    List<Disponibilidad> findByProgramadorIdAndActivoTrue(UUID programadorId);

}