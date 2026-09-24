package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.model.Cafe;
import com.aromaorigem.aromaorigem.model.VarianteCafe;
import com.aromaorigem.aromaorigem.repository.CafeRepository;
import com.aromaorigem.aromaorigem.repository.VarianteCafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CafeService {
    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private VarianteCafeRepository varianteCafeRepository;

    public List<Cafe> listarTodos() {
        return cafeRepository.findAllWithVariantes();
    }

    public Optional<Cafe> buscarPorId(Long id) {
        return cafeRepository.findByIdWithVariantes(id);
    }

    public Cafe salvarCafe(Cafe cafe) {
        if (cafe.getVariantes() != null) {
            for (VarianteCafe variante : cafe.getVariantes()) {
                variante.setCafe(cafe);
            }
        }
        return cafeRepository.save(cafe);
    }

    public void deletarCafe(Long id) {
        Cafe cafe = cafeRepository.findByIdWithVariantes(id)
                .orElseThrow(() -> new RuntimeException("Café não encontrado ou já inativo"));

        cafe.setAtivo(false);
        cafe.setEmDestaque(false);
        cafeRepository.save(cafe);
    }

    public List<Cafe> filtrarPorRegiao(String regiao) {
        return cafeRepository.findByRegiaoIgnoreCaseWithVariantes(regiao);
    }

    public List<Cafe> filtrarPorAltitudeMinima(Integer altitude) {
        return cafeRepository.findByAltitudeGreaterThanEqualWithVariantes(altitude);
    }

    public VarianteCafe atualizarEstoqueVariante(Long varianteId, Integer novoEstoque) {
        VarianteCafe variante = varianteCafeRepository.findById(varianteId)
                .orElseThrow(() -> new RuntimeException("Variante de café não encontrada"));
        variante.setEstoque(novoEstoque);
        return varianteCafeRepository.save(variante);
    }

    public List<Cafe> listarProdutosPertoDoVencimento() {
        LocalDate daquiUmMes = LocalDate.now().plusDays(30);
        return cafeRepository.findCafesProximosDoVencimento(daquiUmMes);
    }

    public Cafe atualizarCafe(Long id, Cafe cafeAtualizado) {
        Cafe cafeExistente = cafeRepository.findByIdWithVariantes(id)
                .orElseThrow(() -> new RuntimeException("Café não encontrado"));

        cafeExistente.setNome(cafeAtualizado.getNome());
        cafeExistente.setRegiao(cafeAtualizado.getRegiao());
        cafeExistente.setFazendaProdutora(cafeAtualizado.getFazendaProdutora());
        cafeExistente.setProcesso(cafeAtualizado.getProcesso());
        cafeExistente.setAltitude(cafeAtualizado.getAltitude());
        cafeExistente.setIntensidade(cafeAtualizado.getIntensidade());
        cafeExistente.setDescricaoCurta(cafeAtualizado.getDescricaoCurta());
        cafeExistente.setNotasSensoriais(cafeAtualizado.getNotasSensoriais());
        cafeExistente.setImagensUrl(cafeAtualizado.getImagensUrl());
        cafeExistente.setVideoDemonstracaoUrl(cafeAtualizado.getVideoDemonstracaoUrl());
        cafeExistente.setEmDestaque(cafeAtualizado.isEmDestaque());

        if (cafeAtualizado.getVariantes() != null) {
            cafeExistente.getVariantes().clear();
            for (VarianteCafe variante : cafeAtualizado.getVariantes()) {
                variante.setCafe(cafeExistente);
                cafeExistente.getVariantes().add(variante);
            }
        }

        return cafeRepository.save(cafeExistente);
    }
}
