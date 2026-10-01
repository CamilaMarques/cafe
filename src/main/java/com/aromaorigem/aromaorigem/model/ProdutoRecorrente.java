package com.aromaorigem.aromaorigem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos_recorrentes")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private String frequencia;
    private String status;     // "ATIVO" ou "PAUSADO"

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    @JsonIgnore
    private Cafe cafe;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    private LocalDate dataProximaEntrega;
    private Integer contadorFidelidade = 0;

    @ManyToOne
    @JoinColumn(name = "variante_id")
    @JsonIgnore
    private VarianteCafe variante;
}
