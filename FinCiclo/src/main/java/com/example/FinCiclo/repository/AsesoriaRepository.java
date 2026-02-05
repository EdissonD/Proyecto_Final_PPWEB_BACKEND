package com.example.FinCiclo.repository;

import com.example.FinCiclo.entity.Asesoria;
import com.example.FinCiclo.enums.EstadoAsesoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AsesoriaRepository extends JpaRepository<Asesoria, Long> {
    
    List<Asesoria> findByProgramadorId(Long programadorId);
    
    List<Asesoria> findByUsuarioId(Long usuarioId);
    
    List<Asesoria> findByEstado(EstadoAsesoria estado);
    
    List<Asesoria> findByProgramadorIdAndEstado(Long programadorId, EstadoAsesoria estado);
    
    @Query("SELECT a FROM Asesoria a WHERE a.fechaHora BETWEEN :inicio AND :fin")
    List<Asesoria> findByFechaHoraBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT a FROM Asesoria a WHERE a.programador.id = :programadorId " +
           "AND a.fechaHora BETWEEN :inicio AND :fin")
    List<Asesoria> findByProgramadorIdAndFechaHoraBetween(
            @Param("programadorId") Long programadorId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
    
    // Para dashboard - contar asesorías por estado
    long countByEstado(EstadoAsesoria estado);
    
    long countByProgramadorIdAndEstado(Long programadorId, EstadoAsesoria estado);
    
    // Próximas asesorías del programador
    @Query("SELECT a FROM Asesoria a WHERE a.programador.id = :programadorId " +
           "AND a.fechaHora >= :now AND a.estado = :estado " +
           "ORDER BY a.fechaHora ASC")
    List<Asesoria> findProximasAsesorias(
            @Param("programadorId") Long programadorId,
            @Param("now") LocalDateTime now,
            @Param("estado") EstadoAsesoria estado
    );
}