package com.example.FinCiclo.controller;


import com.example.FinCiclo.entity.Usuario;
import com.example.FinCiclo.repository.UsuarioRepository;
import com.example.FinCiclo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {

        if (usuarioRepository.findByEmail(request.get("email")).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email ya está registrado"));
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.get("nombre"));
        usuario.setEmail(request.get("email"));
        usuario.setRol(
                request.get("rol") == null || request.get("rol").isBlank()
                        ? "USUARIO"
                        : request.get("rol")
        );

        String rawPassword = request.get("password");
        if (rawPassword == null || rawPassword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password requerida"));
        }

        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
        usuario.setCreadoEn(LocalDateTime.now());
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", usuario.getRol());
        extraClaims.put("nombre", usuario.getNombre());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                Collections.emptyList()
        );

        String token = jwtService.generateToken(extraClaims, userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.get("email"),
                        request.get("password")
                )
        );

        Usuario user = usuarioRepository.findByEmail(request.get("email"))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", user.getRol());
        extraClaims.put("nombre", user.getNombre());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.emptyList()
        );

        String token = jwtService.generateToken(extraClaims, userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }
}