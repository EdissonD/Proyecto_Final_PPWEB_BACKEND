package com.example.FinCiclo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.FinCiclo.entity.Usuario;
import com.example.FinCiclo.repository.UsuarioRepository;
import com.example.FinCiclo.security.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository userRepository;

    /**
     * Spring Security llama a este método en el login
     * username = email (como definimos antes)
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado con email: " + email
                        )
                );

        return UserDetailsImpl.build(user);
    }
}