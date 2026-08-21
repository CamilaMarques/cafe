package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {
    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes")
    List<Cafe> findAllWithVariantes();

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE c.id = :id")
    Optional<Cafe> findByIdWithVariantes(Long id);

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE c.emDestaque = true")
    List<Cafe> findByEmDestaqueTrueWithVariantes();

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE LOWER(c.regiao) LIKE LOWER(CONCAT('%', :regiao, '%'))")
    List<Cafe> findByRegiaoIgnoreCaseWithVariantes(String regiao);

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.variantes WHERE c.altitude >= :altitude")
    List<Cafe> findByAltitudeGreaterThanEqualWithVariantes(Integer altitude);
}
