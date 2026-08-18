package com.aromaorigem.aromaorigem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "produtos_recorrentes")
@Data
public class ProdutoRecorrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double preco;
    private Integer quantidade;

    private String tipo; // "ASSINATURA" ou "PRODUTO"
    private String permanenciaMinima;
    private Integer diaEntrega;
    private String imagemUrl;
    private String frequencia; // "MENSAL" ou "QUINZENAL"
    private String status;     // "ATIVO" ou "PAUSADO"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}
