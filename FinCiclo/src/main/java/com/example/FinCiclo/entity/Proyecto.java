package com.example.FinCiclo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "proyectos")
public class Proyecto {

    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    private String titulo;
    private String descripcion;
    private String tecnologias;

    @Column(name = "url_demo")
    private String urlDemo;

    @Column(name = "url_repo")
    private String urlRepo;

    private String estado;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
}