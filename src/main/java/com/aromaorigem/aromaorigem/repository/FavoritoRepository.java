package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.Favorito;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByUsuarioIdAndCafeId(Long usuarioId, Long cafeId);
    boolean existsByUsuarioIdAndCafeId(Long usuarioId, Long cafeId);

    @Transactional
    @Modifying
    void deleteByUsuarioIdAndCafeId(Long usuarioId, Long cafeId);
}