package com.aromaorigem.aromaorigem.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String sobrenome;

    @Column(name = "nome_social")
    private String nomeSocial; // Não obrigatório, respeita a identidade da pessoa

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String senha;

    @JsonIgnore
    private String role;

    @Column(unique = true)
    private String cpf;

    private String celular;
    private String dataNascimento;

    private LocalDateTime dataCriacao = LocalDateTime.now();

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

    @Column(name = "contador_fidelidade")
    private Integer contadorFidelidade = 0;

    @Column(name = "ciente_mudanca_plano")
    private boolean cienteMudancaPlano = false;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProdutoRecorrente> produtosRecorrentes;

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return senha;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @JsonIgnore
    @Override public boolean isAccountNonExpired() { return true; }

    @JsonIgnore
    @Override public boolean isAccountNonLocked() { return true; }

    @JsonIgnore
    @Override public boolean isCredentialsNonExpired() { return true; }

    @JsonIgnore
    @Override public boolean isEnabled() { return true; }
}
