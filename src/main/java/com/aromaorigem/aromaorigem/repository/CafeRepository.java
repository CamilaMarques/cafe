package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {
    List<Cafe> findByRegiaoIgnoreCase(String regiao);

    List<Cafe> findByAltitudeGreaterThanEqual(Integer altitude);

    List<Cafe> findByProcessoIgnoreCase(String processo);
}
