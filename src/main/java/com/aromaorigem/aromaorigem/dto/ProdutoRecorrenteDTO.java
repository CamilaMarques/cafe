package com.aromaorigem.aromaorigem.dto;

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
        String status
) {}