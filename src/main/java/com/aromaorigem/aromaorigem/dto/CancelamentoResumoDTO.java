package com.aromaorigem.aromaorigem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelamentoResumoDTO {
    private boolean isentoMulta;
    private String motivoIsencao; // Ex: "Direito de arrependimento (7 dias)" ou "Permanência mínima atingida"
    private int mesesCumpridos;
    private int mesesRestantes;
    private BigDecimal valorMensalidade;
    private BigDecimal saldoRestanteContrato;
    private BigDecimal valorMulta; // 10% sobre o saldo restante
    private String mensagem;
    private boolean produtoJaEnviado;
}