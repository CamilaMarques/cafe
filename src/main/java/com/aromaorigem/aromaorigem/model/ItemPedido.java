package com.aromaorigem.aromaorigem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Pedido pedido;

    private String nome;
    private String peso;
    private String fazendaProdutora;
    private String regiao;
    private Integer altitude;
    private Integer intensidade;
    private BigDecimal precoUnitario;
    private Integer quantidade;
}