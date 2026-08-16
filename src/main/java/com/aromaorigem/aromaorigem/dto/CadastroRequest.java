package com.aromaorigem.aromaorigem.dto;

public record CadastroRequest(
        String nome,
        String email,
        String senha,
        String cep,
        String rua,
        String numero,
        String cidade,
        String estado,
        String complemento
) {}