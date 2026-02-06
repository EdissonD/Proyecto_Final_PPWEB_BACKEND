package com.marcelo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marcelo.backend.models.Usuario;

import java.util.Optional; // Importar esto
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    // Método necesario para el Login
    Optional<Usuario> findByEmail(String email);
}