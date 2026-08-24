package com.aromaorigem.aromaorigem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesJornadaDTO {
    private int numero;
    private String mesAno;
    private String status;
    private String titulo;
    private String descricao;
}