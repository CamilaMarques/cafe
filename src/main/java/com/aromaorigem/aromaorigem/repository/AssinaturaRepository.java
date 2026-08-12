package com.aromaorigem.aromaorigem.repository;

import com.aromaorigem.aromaorigem.model.Assinatura;
import com.aromaorigem.aromaorigem.model.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssinaturaRepository extends JpaRepository <Assinatura, Long> {


}



