package com.marcelo.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.marcelo.backend.models.Notificacion;
import com.marcelo.backend.repositories.NotificacionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @GetMapping
    public List<Notificacion> listar() {
        return notificacionRepository.findAll();
    }
}
