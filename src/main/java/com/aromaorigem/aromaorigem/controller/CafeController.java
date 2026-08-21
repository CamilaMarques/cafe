package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.model.Cafe;
import com.aromaorigem.aromaorigem.repository.AvaliacaoRepository;
import com.aromaorigem.aromaorigem.repository.CafeRepository;
import com.aromaorigem.aromaorigem.service.CafeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
@CrossOrigin(origins = "*")
public class CafeController {
    @Autowired
    private CafeService cafeService;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @GetMapping
    public ResponseEntity<List<Cafe>> listarTodos() {
        List<Cafe> cafes = cafeService.listarTodos();
        return ResponseEntity.ok(cafes);
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<Cafe>> listarDestaques() {
        List<Cafe> destaques = cafeRepository.findByEmDestaqueTrueWithVariantes();
        return ResponseEntity.ok(destaques);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cafe> buscarPorId(@PathVariable Long id) {
        return cafeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cafe> criarCafe(@RequestBody Cafe cafe) {
        Cafe novoCafe = cafeService.salvarCafe(cafe);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCafe);
    }

    @GetMapping("/filtro/regiao")
    public ResponseEntity<List<Cafe>> filtrarPorRegiao(@RequestParam String regiao) {
        List<Cafe> cafes = cafeService.filtrarPorRegiao(regiao);
        return ResponseEntity.ok(cafes);
    }

    @GetMapping("/filtro/altitude")
    public ResponseEntity<List<Cafe>> filtrarPorAltitude(@RequestParam Integer min) {
        List<Cafe> cafes = cafeService.filtrarPorAltitudeMinima(min);
        return ResponseEntity.ok(cafes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCafe(@PathVariable Long id) {
        cafeService.deletarCafe(id);
        return ResponseEntity.noContent().build();
    }
}
