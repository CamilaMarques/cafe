package com.aromaorigem.aromaorigem.model;

import com.aromaorigem.aromaorigem.enums.StatusAssinatura;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assinaturas")
@Data
@NoArgsConstructor
public class Assinatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plano;
    private Double valorMensal;
    private Integer quantidade = 1;

    @Enumerated(EnumType.STRING)
    private StatusAssinatura status = StatusAssinatura.ATIVO;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDate dataProximaEntrega;
    private String peso;
    private Integer mesesPermanenciaMinima = 3;
    private Boolean cicloReiniciadoPorUpgrade = false;
    private Integer contadorFidelidadeClube = 0;

}
