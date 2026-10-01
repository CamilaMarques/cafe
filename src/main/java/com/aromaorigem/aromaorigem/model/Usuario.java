package com.aromaorigem.aromaorigem.model;
import com.aromaorigem.aromaorigem.enums.TipoPlano;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String bairro;
    private String cidade;
    private String estado;
    private String complemento;

    @JsonProperty("bairroPrincipal")
    public String getBairroPrincipal() {
        return bairro;
    }

    @JsonProperty("bairroPrincipal")
    public void setBairroPrincipal(String bairroPrincipal) {
        this.bairro = bairroPrincipal;
    }

    private String cepAlternativo;
    private String ruaAlternativa;
    private String bairroAlternativo;
    private String numeroAlternativo;
    private String cidadeAlternativa;
    private String estadoAlternativo;
    private String complementoAlternativo;

    private String moagemPreferida;
    private String notasSensoriais;
    private String intensidade;

    private String statusAssinatura;

    @Column(name = "ciente_mudanca_plano")
    private boolean cienteMudancaPlano = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "plano_ativo")
    private TipoPlano planoAtivo;

    @Column(name = "data_inicio_plano")
    private LocalDate dataInicioPlano;

    @Column(name = "contador_fidelidade")
    private Integer contadorFidelidade = 0;

    @Column(name = "contador_fidelidade_geral")
    private Integer contadorFidelidadeGeral = 0;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProdutoRecorrente> produtosRecorrentes = new ArrayList<>();

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
        if (this.role == null || this.role.isEmpty()) {
            return Collections.emptyList();
        }
        String formattedRole = this.role.startsWith("ROLE_") ? this.role : "ROLE_" + this.role;
        return Collections.singletonList(new SimpleGrantedAuthority(formattedRole));
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
