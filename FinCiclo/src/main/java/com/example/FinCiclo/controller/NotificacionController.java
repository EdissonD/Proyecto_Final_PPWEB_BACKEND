package com.example.FinCiclo.controller;
import com.example.FinCiclo.entity.Notificacion;
import com.example.FinCiclo.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
