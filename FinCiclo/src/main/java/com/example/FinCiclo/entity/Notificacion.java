package com.example.FinCiclo.entity;

import com.example.FinCiclo.enums.EstadoNotificacion;
import com.example.FinCiclo.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;
    
    @Column(nullable = false, length = 200)
    private String destinatario;
    
    @Column(length = 200)
    private String asunto;
    
    @Column(length = 2000, nullable = false)
    private String mensaje;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asesoria_id")
    private Asesoria asesoria;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;
    
    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;
    
    @Column(name = "fecha_enviada")
    private LocalDateTime fechaEnviada;
    
    @Column(nullable = false)
    private Integer intentos = 0;
    
    @Column(name = "error_mensaje", length = 500)
    private String errorMensaje;
    
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoNotificacion.PENDIENTE;
        }
        if (intentos == null) {
            intentos = 0;
        }
    }
}