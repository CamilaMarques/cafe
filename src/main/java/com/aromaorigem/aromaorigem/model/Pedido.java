package com.aromaorigem.aromaorigem.model;

import com.aromaorigem.aromaorigem.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String resumoItens; // Ex: "1x Café Fazenda Santa Rita, Assinatura Explorador"

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private BigDecimal valorFrete;

    private String formaPagamento; // PIX, Cartao, Boleto

    private String enderecoEntrega;

    private String prazoEntrega;

    private String turnoEntrega;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusPedido status = StatusPedido.EM_TRANSITO; // Padrão inicial para testes

    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
}
