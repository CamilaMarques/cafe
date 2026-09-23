package com.aromaorigem.aromaorigem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemPedidoDTO {
    private String nome;
    private String peso;
    private String fazendaProdutora;
    private String regiao;
    private Integer altitude;
    private Integer intensidade;
    private BigDecimal precoUnitario;
    private Integer quantidade;
}
