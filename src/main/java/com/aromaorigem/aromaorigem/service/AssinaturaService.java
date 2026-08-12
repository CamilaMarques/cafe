package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.model.Assinatura;
import com.aromaorigem.aromaorigem.repository.AssinaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssinaturaService {
    @Autowired
    private AssinaturaRepository assinaturaRepository;

    public List<Assinatura> listarTodas() {
        return assinaturaRepository.findAll();
    }

    public Optional<Assinatura> buscarPorId(Long id) {
        return assinaturaRepository.findById(id);
    }

    public Assinatura salvarAssinatura(Assinatura assinatura) {
        return assinaturaRepository.save(assinatura);
    }

    public void deletarAssinatura(Long id) {
        assinaturaRepository.deleteById(id);
    }
}
