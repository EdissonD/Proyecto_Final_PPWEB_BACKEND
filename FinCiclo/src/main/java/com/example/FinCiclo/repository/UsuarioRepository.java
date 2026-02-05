package com.example.FinCiclo.repository;

import com.example.FinCiclo.entity.Proyecto;
import com.example.FinCiclo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;



public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método necesario para el Login
    Optional<Usuario> findByEmail(String email);
    List<Proyecto> findByUsuarioId(long id);

}