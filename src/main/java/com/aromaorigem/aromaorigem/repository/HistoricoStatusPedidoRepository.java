package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.HistoricoStatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoStatusPedidoRepository extends JpaRepository<HistoricoStatusPedido, Long> {
}