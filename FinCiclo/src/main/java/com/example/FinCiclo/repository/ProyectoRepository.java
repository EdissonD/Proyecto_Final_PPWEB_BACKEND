package com.example.FinCiclo.repository;

import com.example.FinCiclo.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; 
import java.util.UUID;

public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {
    // Spring Data JPA es inteligente: entiende que buscas por el ID del objeto 'programador'
    List<Proyecto> findByProgramadorId(UUID programadorId);
    List<Proyecto> findByUsuarioId(UUID id);

}