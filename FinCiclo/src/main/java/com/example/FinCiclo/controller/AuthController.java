package com.example.FinCiclo.controller;

import com.example.FinCiclo.dto.AuthResponse;
import com.example.FinCiclo.dto.LoginRequest;
import com.example.FinCiclo.dto.RegisterRequest;
import com.example.FinCiclo.entity.Role;
import com.example.FinCiclo.entity.Usuario;
import com.example.FinCiclo.enums.RoleName;
import com.example.FinCiclo.repository.RoleRepository;
import com.example.FinCiclo.repository.UsuarioRepository;
import com.example.FinCiclo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {

        // Validar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("El email ya está registrado"));
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setName(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEnabled(true);

        // 🔹 Determinar rol - por defecto ROLE_USER
        RoleName roleName = RoleName.ROLE_USER; // Valor por defecto
        
        if (request.getRol() != null && !request.getRol().isEmpty()) {
            try {
                // Asegurarse que el rol tenga el prefijo ROLE_
                String rolString = request.getRol().toUpperCase();
                if (!rolString.startsWith("ROLE_")) {
                    rolString = "ROLE_" + rolString;
                }
                roleName = RoleName.valueOf(rolString);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("Rol inválido: " + request.getRol()));
            }
        }

        // ✅ CORRECCIÓN: Usar la variable roleName directamente
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));

        usuario.setRoles(Set.of(role));
        usuarioRepository.save(usuario);

        // Generar JWT
        User userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority(roleName.name()))
        );

        String jwt = jwtService.generateToken(new HashMap<>(), userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        try {
            // Autenticar usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Credenciales incorrectas"));
        }

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ✅ CORRECCIÓN: Agregar .name() para convertir enum a String
        User userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                        .toList()
        );

        // Generar JWT
        String jwt = jwtService.generateToken(new HashMap<>(), userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}