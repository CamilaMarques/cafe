package com.aromaorigem.aromaorigem.dto;

public record CadastroRequest(
        String nome,
        String email,
        String senha,
        String cpf,
        String celular,
        String dataNascimento,

        String cep,
        String rua,
        String numero,
        String cidade,
        String estado,
        String complemento,

        String cepAlternativo,
        String ruaAlternativo,
        String numeroAlternativo,
        String cidadeAlternativo,
        String estadoAlternativo,
        String complementoAlternativo,


        String moagemPreferida,
        String notasSensoriais,
        String intensidade
) {}