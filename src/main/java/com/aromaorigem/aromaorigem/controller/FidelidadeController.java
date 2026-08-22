package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.service.FidelidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/fidelidade")
@CrossOrigin(origins = "*")
public class FidelidadeController {

    @Autowired
    private FidelidadeService fidelidadeService;

    @GetMapping("/progresso")
    public ResponseEntity<Map<String, Object>> obterProgresso(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).build();
        }

        Map<String, Object> progresso = fidelidadeService.obterProgressoFidelidade(usuarioLogado);
        return ResponseEntity.ok(progresso);
    }
}