package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.model.Assinatura;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.service.AssinaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assinaturas")
@CrossOrigin(origins = "*")
public class AssinaturaController {

    @Autowired
    private AssinaturaService assinaturaService;

    @GetMapping
    public ResponseEntity<List<Assinatura>> listarTodas() {
        return ResponseEntity.ok(assinaturaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assinatura> buscarPorId(@PathVariable Long id) {
        return assinaturaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Assinatura> criarAssinatura(
            @RequestBody Assinatura assinatura,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Assinatura novaAssinatura = assinaturaService.salvarAssinatura(assinatura, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaAssinatura);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAssinatura(@PathVariable Long id) {
        assinaturaService.deletarAssinatura(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<Assinatura> atualizarQuantidade(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Integer> payload) {

        Integer novaQuantidade = payload.get("quantidade");
        Assinatura assinaturaAtualizada = assinaturaService.atualizarQuantidade(id, novaQuantidade);
        return ResponseEntity.ok(assinaturaAtualizada);
    }

}
