package com.example.FinCiclo.repository;

import com.example.FinCiclo.entity.Proyecto;
import com.example.FinCiclo.enums.EstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    
    // ✅ CORREGIDO: Cambiado de UUID a Long
    List<Proyecto> findByProgramadorId(Long programadorId);
    
    List<Proyecto> findByUsuarioId(Long usuarioId);
    
    List<Proyecto> findByEstado(EstadoProyecto estado);
    
    List<Proyecto> findByUsuarioIdAndEstado(Long usuarioId, EstadoProyecto estado);
    
    @Query("SELECT p FROM Proyecto p WHERE p.programador.id = :programadorId AND p.estado = :estado")
    List<Proyecto> findByProgramadorIdAndEstado(
            @Param("programadorId") Long programadorId,
            @Param("estado") EstadoProyecto estado
    );
    
    // Contar proyectos activos de un usuario
    long countByUsuarioIdAndEstado(Long usuarioId, EstadoProyecto estado);
    
    // Buscar por nombre (para búsquedas)
    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);
}