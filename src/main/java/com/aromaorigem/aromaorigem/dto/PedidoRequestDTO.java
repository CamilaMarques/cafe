package com.aromaorigem.aromaorigem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private String tipo;
    private String plano;
    private String frequencia;
    private BigDecimal precoUnitario;
    private Integer quantidade;
    private List<ItemPedidoDTO> itens;
    private BigDecimal valorTotal;
    private BigDecimal valorFrete;
    private String formaPagamento;
    private String enderecoEntrega;
    private String prazoEntrega;
    private String turnoEntrega;

    public List<ItemPedidoDTO> getItems() {
        return this.itens;
    }

    public void setItems(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }
}
