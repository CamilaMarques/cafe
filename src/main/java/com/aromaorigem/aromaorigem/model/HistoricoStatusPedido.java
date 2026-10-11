package com.aromaorigem.aromaorigem.model;

import com.aromaorigem.aromaorigem.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_status_pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoStatusPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    private String responsavel;

    @Builder.Default
    private LocalDateTime dataAlteracao = LocalDateTime.now();
}