package com.aromaorigem.aromaorigem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cafes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String regiao;

    @Column(nullable = false)
    private String processo;

    @Column(nullable = false)
    private Integer altitude;

    @Column(nullable = false)
    private Double preco;

    @ElementCollection
    @CollectionTable(name = "cafe_notas", joinColumns = @JoinColumn(name = "cafe_id"))
    @Column(name = "nota")
    private List<String> notasSensoriais;
}
