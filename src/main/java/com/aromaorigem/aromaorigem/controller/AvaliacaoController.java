package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.model.Avaliacao;
import com.aromaorigem.aromaorigem.model.Cafe;
import com.aromaorigem.aromaorigem.repository.AvaliacaoRepository;
import com.aromaorigem.aromaorigem.repository.CafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "*")
public class AvaliacaoController {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private CafeRepository cafeRepository;

    @GetMapping("/cafe/{cafeId}")
    public ResponseEntity<List<Avaliacao>> listarPorCafe(@PathVariable Long cafeId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByCafeId(cafeId);
        return ResponseEntity.ok(avaliacoes);
    }

    @PostMapping
    public ResponseEntity<?> criarAvaliacao(@RequestBody Avaliacao avaliacao) {
        if (avaliacao.getCafe() == null || avaliacao.getCafe().getId() == null) {
            return ResponseEntity.badRequest().body("O ID do café é obrigatório para avaliar.");
        }

        Optional<Cafe> cafeOpt = cafeRepository.findById(avaliacao.getCafe().getId());
        if (cafeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Café não encontrado.");
        }

        Cafe cafe = cafeOpt.get();
        avaliacao.setCafe(cafe);

        Avaliacao novaAvaliacao = avaliacaoRepository.save(avaliacao);

        List<Avaliacao> todasDoCafe = avaliacaoRepository.findByCafeId(cafe.getId());
        double media = todasDoCafe.stream().mapToInt(Avaliacao::getNota).average().orElse(0.0);

        cafe.setMediaNotas(Math.round(media * 10.0) / 10.0);
        cafe.setTotalAvaliacoes(todasDoCafe.size());
        cafeRepository.save(cafe);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaAvaliacao);
    }
}
