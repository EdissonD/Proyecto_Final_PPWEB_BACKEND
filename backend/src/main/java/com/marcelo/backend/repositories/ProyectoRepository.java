package com.marcelo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.marcelo.backend.models.Proyecto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {

    // ✅ el tuyo
    List<Proyecto> findByProgramadorId(UUID programadorId);

    // =========================
    // ✅ NUEVO: REPORTES ADMIN
    // =========================

    /**
     * Dashboard: proyectos agrupados por programador.
     * Activo = estado == 'activo' (case-insensitive)
     * Inactivo = todo lo demás (incluye null)
     */
    @Query("""
        select
          p.programador.id,
          coalesce(p.programador.usuario.nombre, 'Programador'),
          sum(case when lower(coalesce(p.estado,'sin_estado')) = 'activo' then 1 else 0 end),
          sum(case when lower(coalesce(p.estado,'sin_estado')) <> 'activo' then 1 else 0 end),
          count(p)
        from Proyecto p
        where (:from is null or p.creadoEn >= :from)
          and (:to is null or p.creadoEn <= :to)
          and (:programadorId is null or p.programador.id = :programadorId)
          and (:estado is null or lower(coalesce(p.estado,'sin_estado')) = lower(:estado))
        group by p.programador.id, p.programador.usuario.nombre
        order by count(p) desc
    """)
    List<Object[]> dashboardProyectos(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("programadorId") UUID programadorId,
            @Param("estado") String estado
    );

    /**
     * Detalle para export: lista de proyectos filtrados.
     */
    @Query("""
        select p
        from Proyecto p
        where (:from is null or p.creadoEn >= :from)
          and (:to is null or p.creadoEn <= :to)
          and (:programadorId is null or p.programador.id = :programadorId)
          and (:estado is null or lower(coalesce(p.estado,'sin_estado')) = lower(:estado))
        order by p.creadoEn desc
    """)
    List<Proyecto> detalleProyectos(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("programadorId") UUID programadorId,
            @Param("estado") String estado
    );
}
