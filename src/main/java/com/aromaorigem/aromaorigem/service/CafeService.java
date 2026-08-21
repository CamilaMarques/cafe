package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.model.Cafe;
import com.aromaorigem.aromaorigem.repository.CafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CafeService {
    @Autowired
    private CafeRepository cafeRepository;

    public List<Cafe> listarTodos() {
        return cafeRepository.findAllWithVariantes();
    }

    public Optional<Cafe> buscarPorId(Long id) {
        return cafeRepository.findByIdWithVariantes(id);
    }

    public Cafe salvarCafe(Cafe cafe) {
        return cafeRepository.save(cafe);
    }

    public void deletarCafe(Long id) {
        cafeRepository.deleteById(id);
    }

    public List<Cafe> filtrarPorRegiao(String regiao) {
        return cafeRepository.findByRegiaoIgnoreCaseWithVariantes(regiao);
    }

    public List<Cafe> filtrarPorAltitudeMinima(Integer altitude) {
        return cafeRepository.findByAltitudeGreaterThanEqualWithVariantes(altitude);
    }
}
