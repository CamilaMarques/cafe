package com.aromaorigem.aromaorigem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "favoritos")
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Long cafeId;
}
