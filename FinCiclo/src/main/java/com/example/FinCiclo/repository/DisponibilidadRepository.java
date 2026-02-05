package com.example.FinCiclo.repository;

import com.example.FinCiclo.entity.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    
    List<Disponibilidad> findByProgramadorId(Long programadorId);
    
    List<Disponibilidad> findByProgramadorIdAndActivoTrue(Long programadorId);
    
    @Query("SELECT d FROM Disponibilidad d WHERE d.programador.id = :programadorId " +
           "AND d.diaSemana = :diaSemana AND d.activo = true")
    List<Disponibilidad> findByProgramadorIdAndDiaSemana(
            @Param("programadorId") Long programadorId,
            @Param("diaSemana") DayOfWeek diaSemana
    );
    
    // Obtener disponibilidades activas de todos los programadores
    List<Disponibilidad> findByActivoTrue();
}