package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.enums.StatusAssinatura;
import com.aromaorigem.aromaorigem.model.Assinatura;
import com.aromaorigem.aromaorigem.model.Cafe;
import com.aromaorigem.aromaorigem.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {

    List<Assinatura> findByUsuario(Usuario usuario);

    Optional<Assinatura> findFirstByUsuarioAndStatusOrderByDataCriacaoDesc(Usuario usuario, StatusAssinatura status);

    boolean existsByUsuarioAndStatus(Usuario usuario, StatusAssinatura status);

    @Query("SELECT a FROM Assinatura a LEFT JOIN FETCH a.usuario")
    List<Assinatura> findAllComUsuario();
}


