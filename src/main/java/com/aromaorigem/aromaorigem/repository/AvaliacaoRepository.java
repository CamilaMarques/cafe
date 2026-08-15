package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByCafeId(Long cafeId);
    boolean existsByUsuarioIdAndCafeId(Long usuarioId, Long cafeId);
}