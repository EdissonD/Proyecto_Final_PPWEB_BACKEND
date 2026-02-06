package com.marcelo.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marcelo.backend.dto.ProgramadorCrearActualizarDTO;
import com.marcelo.backend.dto.ProgramadorPublicoDTO;
import com.marcelo.backend.models.Programador;
import com.marcelo.backend.models.Usuario;
import com.marcelo.backend.repositories.ProgramadorRepository;
import com.marcelo.backend.repositories.UsuarioRepository;

import java.util.*;

@RestController
@RequestMapping("/api/programadores")
@CrossOrigin(origins = "*")
public class ProgramadorController {

    @Autowired
    private ProgramadorRepository programadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- GET METHODS ---
    @GetMapping
    public List<ProgramadorPublicoDTO> obtenerTodos() {
        return programadorRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    @GetMapping("/{id}")
    public ProgramadorPublicoDTO obtenerUno(@PathVariable UUID id) {
        Programador p = programadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        return convertirADTO(p);
    }

    // --- ✅ CREAR (POST JSON) ---
    @PostMapping
    public ResponseEntity<?> crearProgramador(@RequestBody ProgramadorCrearActualizarDTO dto) {
        try {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nombre es requerido"));
            }
            if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Descripción es requerida"));
            }
            if (dto.getEspecialidad() == null || dto.getEspecialidad().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Especialidad es requerida"));
            }

            String emailReal = (dto.getEmailContacto() != null && !dto.getEmailContacto().isBlank())
                    ? dto.getEmailContacto()
                    : "temp_" + UUID.randomUUID() + "@sistema.com";

            if (usuarioRepository.findByEmail(emailReal).isPresent()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "El email " + emailReal + " ya está registrado.")
                );
            }

            // 1) USUARIO
            Usuario usuario = new Usuario();
            usuario.setNombre(dto.getNombre());
            usuario.setEmail(emailReal);
            usuario.setPasswordHash("123456");
            usuario.setRol("programador");
            usuario.setActivo(true);

            // ✅ fotoUrl viene desde Angular (Cloudinary)
            usuario.setFotoUrl(dto.getFotoUrl());

            usuario = usuarioRepository.save(usuario);

            // 2) PROGRAMADOR
            Programador p = new Programador();
            p.setUsuario(usuario);
            p.setEspecialidad(dto.getEspecialidad());
            p.setDescripcion(dto.getDescripcion());

            p.setEmailContacto(dto.getEmailContacto());
            p.setGithub(dto.getGithub());
            p.setLinkedin(dto.getLinkedin());
            p.setPortafolio(dto.getPortafolio());
            p.setWhatsapp(dto.getWhatsapp());

            p.setDisponibilidadTexto(dto.getDisponibilidad());

            if (dto.getHorasDisponibles() != null) {
                p.setHorasDisponibles(dto.getHorasDisponibles());
            } else {
                p.setHorasDisponibles(Collections.emptyList());
            }

            return ResponseEntity.ok(programadorRepository.save(p));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error al crear: " + e.getMessage()));
        }
    }

    // --- ✅ ACTUALIZAR (PUT JSON) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProgramador(
            @PathVariable UUID id,
            @RequestBody ProgramadorCrearActualizarDTO dto
    ) {
        try {
            Programador p = programadorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("No existe"));

            if (dto.getDescripcion() != null) p.setDescripcion(dto.getDescripcion());
            if (dto.getEspecialidad() != null) p.setEspecialidad(dto.getEspecialidad());

            p.setEmailContacto(dto.getEmailContacto());
            p.setGithub(dto.getGithub());
            p.setLinkedin(dto.getLinkedin());
            p.setPortafolio(dto.getPortafolio());
            p.setWhatsapp(dto.getWhatsapp());
            p.setDisponibilidadTexto(dto.getDisponibilidad());

            if (dto.getHorasDisponibles() != null) {
                p.setHorasDisponibles(dto.getHorasDisponibles());
            }

            Usuario u = p.getUsuario();
            if (u != null) {
                if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
                    u.setNombre(dto.getNombre());
                }
                // ✅ si viene fotoUrl, actualiza fotoUrl
                if (dto.getFotoUrl() != null && !dto.getFotoUrl().isBlank()) {
                    u.setFotoUrl(dto.getFotoUrl());
                }
                usuarioRepository.save(u);
            }

            return ResponseEntity.ok(programadorRepository.save(p));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al editar: " + e.getMessage()));
        }
    }

    // --- ✅ ELIMINAR (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProgramador(@PathVariable UUID id) {
        try {
            Programador p = programadorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("No existe el programador"));

            Usuario u = p.getUsuario();

            programadorRepository.delete(p);

            if (u != null) {
                usuarioRepository.delete(u);
            }

            return ResponseEntity.ok(Map.of("mensaje", "Programador eliminado correctamente"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al eliminar: " + e.getMessage()));
        }
    }

    // --- CONVERSOR DTO ---
    private ProgramadorPublicoDTO convertirADTO(Programador p) {
        String nombre = (p.getUsuario() != null) ? p.getUsuario().getNombre() : "Sin Nombre";
        String foto = (p.getUsuario() != null) ? p.getUsuario().getFotoUrl() : null;
        UUID usuarioId = (p.getUsuario() != null) ? p.getUsuario().getId() : null;

        return new ProgramadorPublicoDTO(
                p.getId(),
                nombre,
                foto,
                p.getEspecialidad(),
                p.getDescripcion(),
                p.getDisponibilidadTexto(),
                p.getHorasDisponibles(),
                usuarioId
        );
    }
}
