package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE c.ativo = true")
    List<Cafe> findAllWithVariantes();

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE c.id = :id AND c.ativo = true")
    Optional<Cafe> findByIdWithVariantes(Long id);

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE c.emDestaque = true AND c.ativo = true")
    List<Cafe> findByEmDestaqueTrueWithVariantes();

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE LOWER(c.regiao) LIKE LOWER(CONCAT('%', :regiao, '%')) AND c.ativo = true")
    List<Cafe> findByRegiaoIgnoreCaseWithVariantes(String regiao);

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE c.altitude >= :altitude AND c.ativo = true")
    List<Cafe> findByAltitudeGreaterThanEqualWithVariantes(Integer altitude);

    @Query("SELECT c FROM Cafe c JOIN c.variantes v WHERE v.dataValidade <= :dataLimite AND v.estoque > 0 AND c.ativo = true")
    List<Cafe> findCafesProximosDoVencimento(@Param("dataLimite") LocalDate dataLimite);
}