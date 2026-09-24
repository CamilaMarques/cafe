package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.Cafe;
import com.aromaorigem.aromaorigem.model.VarianteCafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VarianteCafeRepository extends JpaRepository <VarianteCafe, Long>{
    @Query("SELECT c FROM Cafe c JOIN c.variantes v WHERE v.dataValidade <= :dataLimite AND v.estoque > 0")
    List<Cafe> findCafesProximosDoVencimento(@Param("dataLimite") LocalDate dataLimite);

}
