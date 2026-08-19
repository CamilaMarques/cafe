package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.ProdutoRecorrente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRecorrenteRepository extends JpaRepository<ProdutoRecorrente, Long> {
    List<ProdutoRecorrente> findByUsuarioId(Long usuarioId);
    List<ProdutoRecorrente> findByUsuarioIdAndTipo(Long usuarioId, String tipo);
}
