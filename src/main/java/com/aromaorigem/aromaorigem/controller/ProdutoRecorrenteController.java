package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.ProdutoRecorrenteDTO;
import com.aromaorigem.aromaorigem.model.ProdutoRecorrente;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.ProdutoRecorrenteRepository;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import com.aromaorigem.aromaorigem.service.ProdutoRecorrenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos-recorrentes")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ProdutoRecorrenteController {

    @Autowired
    private ProdutoRecorrenteRepository produtoRecorrenteRepository;

    @Autowired
    private ProdutoRecorrenteService produtoRecorrenteService;

    @GetMapping
    public ResponseEntity<?> listarProdutosDoUsuarioLogado(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
        List<ProdutoRecorrente> produtos = produtoRecorrenteRepository.findByUsuarioId(usuarioLogado.getId());
        return ResponseEntity.ok(produtos);
    }

    @PostMapping
    public ResponseEntity<?> criarAssinaturaProduto(@RequestBody ProdutoRecorrenteDTO novoDto, @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");

        ProdutoRecorrenteDTO salvo = produtoRecorrenteService.criarAssinatura(novoDto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarAssinaturaProduto(@PathVariable Long id) {
        if (produtoRecorrenteRepository.existsById(id)) {
            produtoRecorrenteRepository.deleteById(id);
            return ResponseEntity.ok("Assinatura cancelada com sucesso!");
        }
        return ResponseEntity.notFound().build();
    }
}
