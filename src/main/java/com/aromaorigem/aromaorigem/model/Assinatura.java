package com.aromaorigem.aromaorigem.model;

import com.aromaorigem.aromaorigem.enums.StatusAssinatura;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "assinaturas")
@Data
@NoArgsConstructor
public class Assinatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plano;

    @Enumerated(EnumType.STRING)
    private StatusAssinatura status;
}
