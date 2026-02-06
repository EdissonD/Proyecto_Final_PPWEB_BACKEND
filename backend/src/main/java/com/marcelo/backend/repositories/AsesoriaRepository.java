package com.marcelo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.marcelo.backend.models.Asesoria;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.Map;

public interface AsesoriaRepository extends JpaRepository<Asesoria, UUID> {

    // =========================
    // MÉTODOS ESTÁNDAR (LOS TUYOS)
    // =========================

    List<Asesoria> findByProgramadorId(UUID programadorId);

    // Para saber horas ocupadas en una fecha
    List<Asesoria> findByProgramadorIdAndFecha(UUID programadorId, LocalDate fecha);

    List<Asesoria> findByProgramadorIdAndEstado(UUID programadorId, String estado);

    List<Asesoria> findByProgramadorIdAndFechaBetween(UUID programadorId, LocalDate desde, LocalDate hasta);

    List<Asesoria> findByProgramadorIdAndEstadoAndFechaBetween(UUID programadorId, String estado, LocalDate desde, LocalDate hasta);

    List<Asesoria> findByUsuarioId(UUID usuarioId);

    // ESTE ES EL QUE PEDISTE (Ya estaba, lo mantenemos aquí)
    List<Asesoria> findByProgramadorIdOrderByFechaAscHoraAsc(UUID programadorId);

    List<Asesoria> findByProgramadorIdAndFechaAndEstadoNot(UUID programadorId, LocalDate fecha, String estado);

    Optional<Asesoria> findByProgramadorIdAndFechaAndHora(UUID programadorId, LocalDate fecha, LocalTime hora);

    boolean existsByProgramadorIdAndFechaAndHoraAndEstadoNot(UUID programadorId, LocalDate fecha, LocalTime hora, String estado);

    long countByProgramadorIdAndEstado(UUID programadorId, String estado);

    long countByProgramadorId(UUID programadorId);

    // Serie por fecha (para gráfico)
    @Query("""
        SELECT a.fecha as fecha, COUNT(a) as total
        FROM Asesoria a
        WHERE a.programador.id = :programadorId
        GROUP BY a.fecha
        ORDER BY a.fecha
    """)
    List<Map<String, Object>> countPorFecha(@Param("programadorId") UUID programadorId);


    // =========================
    // ✅ NUEVO: REPORTES ADMIN
    // =========================

    /**
     * Dashboard: asesorías agrupadas por programador, con conteos por estado.
     * Filtros opcionales: from/to, programadorId, estado.
     * - Si estado viene, aplica filtro y los conteos siguen mostrando total filtrado.
     */
    @Query("""
        select
          a.programador.id,
          coalesce(a.programador.usuario.nombre, 'Programador'),
          sum(case when lower(coalesce(a.estado,'pendiente')) = 'pendiente' then 1 else 0 end),
          sum(case when lower(coalesce(a.estado,'')) = 'aprobada' then 1 else 0 end),
          sum(case when lower(coalesce(a.estado,'')) = 'rechazada' then 1 else 0 end),
          count(a)
        from Asesoria a
        where (:from is null or a.fecha >= :from)
          and (:to is null or a.fecha <= :to)
          and (:programadorId is null or a.programador.id = :programadorId)
          and (:estado is null or lower(a.estado) = lower(:estado))
        group by a.programador.id, a.programador.usuario.nombre
        order by count(a) desc
    """)
    List<Object[]> dashboardAsesorias(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("programadorId") UUID programadorId,
            @Param("estado") String estado
    );

    /**
     * Detalle para export: lista de asesorías filtradas.
     */
    @Query("""
        select a
        from Asesoria a
        where (:from is null or a.fecha >= :from)
          and (:to is null or a.fecha <= :to)
          and (:programadorId is null or a.programador.id = :programadorId)
          and (:estado is null or lower(a.estado) = lower(:estado))
        order by a.fecha desc, a.hora desc
    """)
    List<Asesoria> detalleAsesorias(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("programadorId") UUID programadorId,
            @Param("estado") String estado
    );
}
