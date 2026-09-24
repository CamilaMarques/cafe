package com.aromaorigem.aromaorigem.dto;

import com.aromaorigem.aromaorigem.enums.TipoPlano;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String sobrenome;
    private String nomeSocial;
    private String email;
    private String role;
    private LocalDateTime dataCriacao;

    private String cpf;
    private String celular;
    private String dataNascimento;

    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String complemento;

    private String cepAlternativo;
    private String ruaAlternativa;
    private String numeroAlternativo;
    private String bairroAlternativo;
    private String cidadeAlternativa;
    private String estadoAlternativo;
    private String complementoAlternativo;

    private String moagemPreferida;
    private String notasSensoriais;
    private String intensidade;
    private TipoPlano planoAtivo;
    private String statusAssinatura;
    private Integer contadorFidelidade;
    private boolean cienteMudancaPlano;
    private LocalDate dataInicioPlano;
    private Integer contadorFidelidadeGeral;

    private List<ProdutoRecorrenteDTO> produtosRecorrentes;

}