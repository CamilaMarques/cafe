package com.aromaorigem.aromaorigem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoClubeDTO {
    private String planoAtivo;
    private String dataInicio;
    private String proximaCobranca;
    private String proximoEnvio;
    private String terminoFidelidade;
    private List<MesJornadaDTO> jornada;
}