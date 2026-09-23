package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.PedidoFidelidadeHistorico;
import com.aromaorigem.aromaorigem.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoFidelidadeHistoricoRepository extends JpaRepository<PedidoFidelidadeHistorico, Long> {

    List<PedidoFidelidadeHistorico> findByUsuario(Usuario usuario);
    long countByUsuario(Usuario usuario);

    Optional<PedidoFidelidadeHistorico> findByPedidoId(Long pedidoId);
}