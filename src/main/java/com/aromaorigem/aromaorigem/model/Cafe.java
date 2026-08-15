package com.aromaorigem.aromaorigem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "cafes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String regiao;

    private String fazendaProdutora;

    @Column(nullable = false)
    private String processo;

    @Column(nullable = false)
    private Integer altitude;

    private Integer intensidade;

    @Column(nullable = false)
    private BigDecimal preco;

    @ElementCollection
    @CollectionTable(name = "cafe_notas", joinColumns = @JoinColumn(name = "cafe_id"))
    @Column(name = "nota")
    private List<String> notasSensoriais;

    @ElementCollection
    @CollectionTable(name = "cafe_imagens", joinColumns = @JoinColumn(name = "cafe_id"))
    @Column(name = "url_imagem")
    private List<String> imagensUrl;

    private String videoDemonstracaoUrl;

    private boolean emDestaque;

    private Double mediaNotas = 0.0;
    private Integer totalAvaliacoes = 0;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes;
}
