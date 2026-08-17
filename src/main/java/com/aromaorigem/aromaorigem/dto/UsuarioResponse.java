package com.aromaorigem.aromaorigem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String role;
    private LocalDateTime dataCriacao;

    private String cpf;
    private String celular;
    private String dataNascimento;

    private String cep;
    private String rua;
    private String numero;
    private String cidade;
    private String estado;
    private String complemento;

    private String cepAlternativo;
    private String ruaAlternativa;
    private String numeroAlternativo;
    private String cidadeAlternativa;
    private String estadoAlternativo;
    private String complementoAlternativo;

    private String moagemPreferida;
    private String notasSensoriais;
    private String intensidade;
    private String planoAtivo;
    private String statusAssinatura;
    private Integer contadorFidelidade;
}