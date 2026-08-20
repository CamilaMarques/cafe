package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.messaging.AssinaturaProducer;
import com.aromaorigem.aromaorigem.model.Assinatura;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.AssinaturaRepository;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssinaturaService {
    @Autowired
    private AssinaturaRepository assinaturaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AssinaturaProducer assinaturaProducer;

    public List<Assinatura> listarTodas() {
        return assinaturaRepository.findAll();
    }

    public Optional<Assinatura> buscarPorId(Long id) {
        return assinaturaRepository.findById(id);
    }

    public Assinatura salvarAssinatura(Assinatura assinatura, Usuario usuarioLogado) {
        if (usuarioLogado != null && assinatura.getPlano() != null) {
            String nomeAssinatura = assinatura.getPlano();

            String novoPlano = "Explorador";
            if (nomeAssinatura.toLowerCase().contains("sommelier")) {
                novoPlano = "Sommelier";
            } else if (nomeAssinatura.toLowerCase().contains("aibiliver") || nomeAssinatura.toLowerCase().contains("conectado")) {
                novoPlano = "Aibiliver (Conectado)";
            }

            if ("Sommelier".equals(novoPlano) && !novoPlano.equals(usuarioLogado.getPlanoAtivo())) {
                usuarioLogado.setContadorFidelidade(0);
            }

            usuarioLogado.setPlanoAtivo(novoPlano);
            usuarioLogado.setStatusAssinatura("ATIVA");
            usuarioRepository.save(usuarioLogado);
        }

        Assinatura novaAssinatura = assinaturaRepository.save(assinatura);

        if (assinaturaProducer != null) {
            assinaturaProducer.enviarEventoAssinatura(novaAssinatura);
        }

        return novaAssinatura;
    }

    public void deletarAssinatura(Long id) {
        assinaturaRepository.deleteById(id);
    }

    public Assinatura atualizarQuantidade(Long id, Integer novaQuantidade) {
        Assinatura assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));

        assinatura.setQuantidade(novaQuantidade);
        return assinaturaRepository.save(assinatura);
    }
}
