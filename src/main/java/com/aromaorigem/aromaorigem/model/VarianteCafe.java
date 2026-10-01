package com.aromaorigem.aromaorigem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "variantes_cafe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarianteCafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id", nullable = false)
    @JsonIgnore
    private Cafe cafe;

    @Column(nullable = false)
    private String peso;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer estoque = 0;

    private LocalDate dataValidade;
}
