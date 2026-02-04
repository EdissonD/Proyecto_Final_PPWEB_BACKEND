package com.example.FinCiclo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.FinCiclo.entity.Proyecto;
import com.example.FinCiclo.entity.Usuario;
import com.example.FinCiclo.repository.ProyectoRepository;
import com.example.FinCiclo.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "*")
public class ProyectoController {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProyectoController(
            ProyectoRepository proyectoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // 🔐 Usuario autenticado
    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // 1️⃣ Obtener todos
    @GetMapping
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    // 2️⃣ Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtenerUno(@PathVariable UUID id) {
        return proyectoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3️⃣ Crear proyecto (dueño = usuario autenticado)
    @PostMapping
    public ResponseEntity<?> crearProyecto(@RequestBody Proyecto proyecto) {
        Usuario usuario = obtenerUsuarioActual();

        proyecto.setUsuario(usuario);
        proyecto.setCreadoEn(LocalDateTime.now());

        if (proyecto.getEstado() == null) {
            proyecto.setEstado("activo");
        }

        return ResponseEntity.ok(proyectoRepository.save(proyecto));
    }

    // 4️⃣ Actualizar proyecto (solo dueño)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProyecto(
            @PathVariable UUID id,
            @RequestBody Proyecto detalles
    ) {
        Proyecto proyecto = proyectoRepository.findById(id).orElse(null);

        if (proyecto == null) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioActual = obtenerUsuarioActual();
        if (!proyecto.getUsuario().getId().equals(usuarioActual.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para editar este proyecto");
        }

        proyecto.setTitulo(detalles.getTitulo());
        proyecto.setDescripcion(detalles.getDescripcion());
        proyecto.setTecnologias(detalles.getTecnologias());
        proyecto.setUrlRepo(detalles.getUrlRepo());
        proyecto.setUrlDemo(detalles.getUrlDemo());

        if (detalles.getEstado() != null) {
            proyecto.setEstado(detalles.getEstado());
        }

        return ResponseEntity.ok(proyectoRepository.save(proyecto));
    }

    // 5️⃣ Eliminar proyecto (solo dueño)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProyecto(@PathVariable UUID id) {
        Proyecto proyecto = proyectoRepository.findById(id).orElse(null);

        if (proyecto == null) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioActual = obtenerUsuarioActual();
        if (!proyecto.getUsuario().getId().equals(usuarioActual.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para eliminar este proyecto");
        }

        proyectoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 6️⃣ Proyectos de un usuario específico
    @GetMapping("/usuario/{id}")
    public List<Proyecto> obtenerPorUsuario(@PathVariable UUID id) {
        return proyectoRepository.findByUsuarioId(id);
    }
}