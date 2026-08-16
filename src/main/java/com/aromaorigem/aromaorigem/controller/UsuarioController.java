package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.UsuarioResponse;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public ResponseEntity<?> obterPerfil() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getDataCriacao()
        );

        return ResponseEntity.ok(response);
    }
}
