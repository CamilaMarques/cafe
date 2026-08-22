package com.aromaorigem.aromaorigem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "pedidos_fidelidade_historico")
public class PedidoFidelidadeHistorico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "tipo_origem")
    private String tipoOrigem; // "CLUBE_MENSAL", "PRODUTO_RECORRENTE", "COMPRA_AVULSA"

    @Column(name = "data_entrega", nullable = false)
    private LocalDate dataEntrega;
}
