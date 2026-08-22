package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.ProdutoRecorrente;
import com.aromaorigem.aromaorigem.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRecorrenteRepository extends JpaRepository<ProdutoRecorrente, Long> {
    List<ProdutoRecorrente> findByUsuarioId(Long usuarioId);
    List<ProdutoRecorrente> findByUsuarioIdAndTipo(Long usuarioId, String tipo);
    List<ProdutoRecorrente> findByUsuario(Usuario usuario);
    List<ProdutoRecorrente> findByUsuarioAndStatus(Usuario usuario, String status);
}
