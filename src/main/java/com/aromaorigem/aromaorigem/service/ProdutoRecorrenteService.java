package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.dto.ProdutoRecorrenteDTO;
import com.aromaorigem.aromaorigem.model.Cafe;
import com.aromaorigem.aromaorigem.model.ProdutoRecorrente;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.CafeRepository;
import com.aromaorigem.aromaorigem.repository.ProdutoRecorrenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ProdutoRecorrenteService {

    @Autowired
    private ProdutoRecorrenteRepository repository;

    @Autowired
    private CafeRepository cafeRepository;

    public ProdutoRecorrenteDTO criarAssinatura(ProdutoRecorrenteDTO dto, Usuario usuario) {

        Cafe cafe = cafeRepository.findById(dto.cafeId())
                .orElseThrow(() -> new RuntimeException("Café não encontrado"));

        double precoComDesconto = cafe.getPreco().doubleValue() * 0.9;
        double precoArredondado = Math.round(precoComDesconto * 100.0) / 100.0;

        ProdutoRecorrente p = new ProdutoRecorrente();
        p.setCafe(cafe);
        p.setNome(cafe.getNome());
        p.setPreco(precoArredondado);
        p.setQuantidade(dto.quantidade());
        p.setTipo(dto.tipo());
        p.setFrequencia(dto.frequencia());
        p.setImagemUrl(cafe.getImagensUrl() != null && !cafe.getImagensUrl().isEmpty() ? cafe.getImagensUrl().get(0) : dto.imagemUrl());

        p.setUsuario(usuario);
        p.setDataCriacao(LocalDateTime.now());
        p.setDataProximaEntrega(LocalDate.now().plusDays(30));
        p.setContadorFidelidade(0);
        p.setStatus("ATIVO");

        ProdutoRecorrente salvo = repository.save(p);

        return new ProdutoRecorrenteDTO(
                salvo.getId(), salvo.getNome(), salvo.getPreco(), salvo.getQuantidade(),
                salvo.getTipo(), salvo.getPermanenciaMinima(), salvo.getDiaEntrega(),
                salvo.getImagemUrl(), salvo.getFrequencia(), salvo.getStatus(),
                salvo.getDataCriacao().toLocalDate(), salvo.getDataProximaEntrega(),
                salvo.getContadorFidelidade(), cafe.getId(),
                cafe.getFazendaProdutora(),
                cafe.getRegiao()
        );
    }
}