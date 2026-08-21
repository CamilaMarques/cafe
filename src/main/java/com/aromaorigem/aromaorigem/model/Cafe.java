package com.aromaorigem.aromaorigem.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "descricao_curta", length = 500)
    private String descricaoCurta;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "notas_sensoriais", columnDefinition = "text[]")
    private List<String> notasSensoriais;

    // Mapeado como array nativo no Postgres
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "imagens_url", columnDefinition = "text[]")
    private List<String> imagensUrl;

    private String videoDemonstracaoUrl;

    private boolean emDestaque;

    private Double mediaNotas = 0.0;
    private Integer totalAvaliacoes = 0;

    @JsonManagedReference
    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VarianteCafe> variantes;

    @JsonManagedReference
    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes;
}