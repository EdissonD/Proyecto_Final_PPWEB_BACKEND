package com.example.FinCiclo.service;

import com.example.FinCiclo.entity.Notificacion;
import com.example.FinCiclo.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificacionScheduler {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionService notificacionService;

    // cada 60 segundos revisa la cola
    @Scheduled(fixedRate = 60000)
    public void procesarPendientes() {
        List<Notificacion> pendientes = notificacionRepository
                .findByEstadoEnvioAndProgramadaParaLessThanEqualOrderByProgramadaParaAsc(
                        "PENDIENTE",
                        LocalDateTime.now()
                );

        for (Notificacion n : pendientes) {
            notificacionService.enviar(n);
        }
    }
}
