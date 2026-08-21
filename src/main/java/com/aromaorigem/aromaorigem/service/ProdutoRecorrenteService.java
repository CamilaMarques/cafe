package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.dto.ProdutoRecorrenteDTO;
import com.aromaorigem.aromaorigem.model.Cafe;
import com.aromaorigem.aromaorigem.model.ProdutoRecorrente;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.model.VarianteCafe;
import com.aromaorigem.aromaorigem.repository.CafeRepository;
import com.aromaorigem.aromaorigem.repository.ProdutoRecorrenteRepository;
import com.aromaorigem.aromaorigem.repository.VarianteCafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ProdutoRecorrenteService {

    @Autowired private ProdutoRecorrenteRepository repository;
    @Autowired private CafeRepository cafeRepository;
    @Autowired private VarianteCafeRepository varianteRepository;

    public ProdutoRecorrenteDTO criarAssinatura(ProdutoRecorrenteDTO dto, Usuario usuario) {

        VarianteCafe variante = varianteRepository.findById(dto.varianteId())
                .orElseThrow(() -> new RuntimeException("Variante não encontrada"));

        Cafe cafe = variante.getCafe();

        double precoComDesconto = variante.getPreco().doubleValue() * 0.9;
        double precoArredondado = Math.round(precoComDesconto * 100.0) / 100.0;

        ProdutoRecorrente p = new ProdutoRecorrente();
        p.setCafe(cafe);
        p.setNome(cafe.getNome() + " - " + variante.getPeso());
        p.setPreco(precoArredondado);
        p.setQuantidade(dto.quantidade());
        p.setTipo(dto.tipo());
        p.setFrequencia(dto.frequencia());
        p.setImagemUrl(cafe.getImagensUrl() != null && !cafe.getImagensUrl().isEmpty()
                ? cafe.getImagensUrl().get(0) : dto.imagemUrl());

        p.setUsuario(usuario);
        p.setDataCriacao(LocalDateTime.now());
        p.setDataProximaEntrega(LocalDate.now().plusDays(30));
        p.setContadorFidelidade(0);
        p.setStatus("ATIVO");

        ProdutoRecorrente salvo = repository.save(p);

        return new ProdutoRecorrenteDTO(
                salvo.getId(), salvo.getNome(), salvo.getPreco(), salvo.getQuantidade(),
                salvo.getTipo(), "3 meses", null, salvo.getImagemUrl(),
                salvo.getFrequencia(), salvo.getStatus(), salvo.getDataCriacao().toLocalDate(),
                salvo.getDataProximaEntrega(), salvo.getContadorFidelidade(),
                cafe.getId(), variante.getId(), cafe.getFazendaProdutora(), cafe.getRegiao(), null
        );
    }

    public ProdutoRecorrenteDTO atualizarQuantidade(Long id, Integer novaQuantidade) {
        ProdutoRecorrente produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setQuantidade(novaQuantidade);
        ProdutoRecorrente salvo = repository.save(produto);

        Cafe cafe = salvo.getCafe();
        return new ProdutoRecorrenteDTO(
                salvo.getId(), salvo.getNome(), salvo.getPreco(), salvo.getQuantidade(),
                salvo.getTipo(), "3 meses", null, salvo.getImagemUrl(),
                salvo.getFrequencia(), salvo.getStatus(),
                salvo.getDataCriacao() != null ? salvo.getDataCriacao().toLocalDate() : null,
                salvo.getDataProximaEntrega(), salvo.getContadorFidelidade(),
                cafe != null ? cafe.getId() : null, null,
                cafe != null ? cafe.getFazendaProdutora() : null,
                cafe != null ? cafe.getRegiao() : null, null
        );
    }
}