package com.example.FinCiclo.repository;

import com.example.FinCiclo.entity.Notificacion;
import com.example.FinCiclo.enums.EstadoNotificacion;
import com.example.FinCiclo.enums.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    List<Notificacion> findByEstado(EstadoNotificacion estado);
    
    List<Notificacion> findByTipo(TipoNotificacion tipo);
    
    List<Notificacion> findByAsesoriaId(Long asesoriaId);
    
    // Notificaciones pendientes para enviar
    @Query("SELECT n FROM Notificacion n WHERE n.estado = :estado " +
           "AND (n.fechaProgramada IS NULL OR n.fechaProgramada <= :now)")
    List<Notificacion> findNotificacionesPendientes(
            @Param("estado") EstadoNotificacion estado,
            @Param("now") LocalDateTime now
    );
    
    // Notificaciones fallidas con reintentos disponibles
    @Query("SELECT n FROM Notificacion n WHERE n.estado = :estado " +
           "AND n.intentos < :maxIntentos")
    List<Notificacion> findNotificacionesFallidasParaReintentar(
            @Param("estado") EstadoNotificacion estado,
            @Param("maxIntentos") int maxIntentos
    );
}