package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.Pedido;
import com.aromaorigem.aromaorigem.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
}