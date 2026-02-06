package com.marcelo.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "programadores")
public class Programador {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER) // opcional, pero ayuda a evitar lazy en usuario
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String especialidad;
    private String descripcion;
    private String telefono;

    private String emailContacto;
    private String whatsapp;
    private String github;
    private String linkedin;
    private String portafolio;

    @Column(columnDefinition = "TEXT")
    private String disponibilidadTexto;

    // ✅ FIX: cargar siempre para que Jackson no falle
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "programador_horas",
            joinColumns = @JoinColumn(name = "programador_id")
    )
    @Column(name = "hora")
    private List<String> horasDisponibles;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }
}
