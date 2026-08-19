package com.aromaorigem.aromaorigem.dto;

import java.time.LocalDate;

public record ProdutoRecorrenteDTO(
        Long id,
        String nome,
        Double preco,
        Integer quantidade,
        String tipo,
        String permanenciaMinima,
        Integer diaEntrega,
        String imagemUrl,
        String frequencia,
        String status,
        LocalDate dataCriacao,
        LocalDate dataProximaEntrega,
        Integer contadorFidelidade,
        Long cafeId,
        String fazendaProdutora,
        String regiao
) {}