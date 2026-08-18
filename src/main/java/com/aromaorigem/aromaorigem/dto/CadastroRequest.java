package com.aromaorigem.aromaorigem.dto;

import com.aromaorigem.aromaorigem.security.validation.ValidCpf;

public record CadastroRequest(
        String nome,
        String sobrenome,
        String nomeSocial, // Opcional
        String email,
        String senha,

        @ValidCpf(message = "O CPF informado é inválido!")
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