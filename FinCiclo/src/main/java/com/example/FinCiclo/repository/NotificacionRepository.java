package com.example.FinCiclo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.FinCiclo.entity.Notificacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    List<Notificacion> findByEstadoEnvioAndProgramadaParaLessThanEqualOrderByProgramadaParaAsc(
            String estadoEnvio,
            LocalDateTime ahora
    );
}
