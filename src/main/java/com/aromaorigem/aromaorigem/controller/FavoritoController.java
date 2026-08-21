package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.model.Favorito;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.FavoritoRepository;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
@CrossOrigin(origins = "*")
public class FavoritoController {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/alternar")
    @Transactional
    public ResponseEntity<Void> alternarFavorito(@RequestBody Map<String, Long> request, Principal principal) {
        Long cafeId = request.get("cafeId");

        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Long usuarioId = usuario.getId();

        if (favoritoRepository.existsByUsuarioIdAndCafeId(usuarioId, cafeId)) {
            favoritoRepository.deleteByUsuarioIdAndCafeId(usuarioId, cafeId);
        } else {
            Favorito novo = new Favorito();
            novo.setUsuarioId(usuarioId);
            novo.setCafeId(cafeId);
            favoritoRepository.save(novo);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verificar/{cafeId}")
    public ResponseEntity<Boolean> verificar(@PathVariable Long cafeId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(false);
        }

        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            return ResponseEntity.ok(false);
        }

        boolean existe = favoritoRepository.existsByUsuarioIdAndCafeId(usuario.getId(), cafeId);
        return ResponseEntity.ok(existe);
    }
}