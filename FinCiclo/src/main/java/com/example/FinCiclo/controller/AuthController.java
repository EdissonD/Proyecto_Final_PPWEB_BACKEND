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
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;   // 👈 IMPORTANTE
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("El email ya está registrado"));
        }

        Usuario usuario = new Usuario();
        usuario.setName(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEnabled(true);

        // 🔹 Rol por defecto
        RoleName roleName = request.getRol() == null
                ? RoleName.ROLE_USER
                : RoleName.valueOf(request.getRol().toUpperCase());

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setRoles(Set.of(role));

        usuarioRepository.save(usuario);

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

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        User userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                        .toList()
        );

        String jwt = jwtService.generateToken(new HashMap<>(), userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
