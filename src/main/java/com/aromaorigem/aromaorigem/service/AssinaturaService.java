package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.dto.CancelamentoResumoDTO;
import com.aromaorigem.aromaorigem.enums.StatusAssinatura;
import com.aromaorigem.aromaorigem.enums.TipoPlano;
import com.aromaorigem.aromaorigem.messaging.AssinaturaProducer;
import com.aromaorigem.aromaorigem.model.Assinatura;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.AssinaturaRepository;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class AssinaturaService {

    @Autowired
    private AssinaturaRepository assinaturaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired(required = false)
    private AssinaturaProducer assinaturaProducer;

    public List<Assinatura> listarTodas() {
        return assinaturaRepository.findAll();
    }

    public Optional<Assinatura> buscarPorId(Long id) {
        return assinaturaRepository.findById(id);
    }

    @Transactional
    public Assinatura salvarAssinatura(Assinatura assinatura, Usuario usuarioLogado) {
        if (usuarioLogado != null && assinatura.getPlano() != null) {
            String nomeAssinatura = assinatura.getPlano();

            String novoPlano = "Explorador";
            if (nomeAssinatura.toLowerCase().contains("sommelier")) {
                novoPlano = "Sommelier";
            } else if (nomeAssinatura.toLowerCase().contains("aibiliver") || nomeAssinatura.toLowerCase().contains("conectado")) {
                novoPlano = "Aibiliver (Conectado)";
            }

            // Verifica se é uma mudança de plano (upgrade/downgrade) em relação ao plano ativo atual
            boolean ehMudancaDePlano = usuarioLogado.getPlanoAtivo() != null &&
                    usuarioLogado.getPlanoAtivo() != TipoPlano.NENHUM &&
                    !usuarioLogado.getPlanoAtivo().name().equalsIgnoreCase(novoPlano);

            if (ehMudancaDePlano) {
                // A alteração de plano reinicia o ciclo de permanência mínima (3 meses) e a fidelidade conforme regra
                assinatura.setCicloReiniciadoPorUpgrade(true);
                assinatura.setDataCriacao(LocalDateTime.now());
                usuarioLogado.setContadorFidelidade(0);
            }

            TipoPlano planoEnum = TipoPlano.valueOf(novoPlano.toUpperCase().replace(" ", "_").replace("(", "").replace(")", ""));
            usuarioLogado.setPlanoAtivo(planoEnum);
            usuarioLogado.setStatusAssinatura("ATIVA");
            usuarioRepository.save(usuarioLogado);

            assinatura.setUsuario(usuarioLogado);
        }

        if (assinatura.getDataCriacao() == null) {
            assinatura.setDataCriacao(LocalDateTime.now());
        }
        if (assinatura.getDataProximaEntrega() == null) {
            assinatura.setDataProximaEntrega(LocalDate.now().plusMonths(1));
        }
        if (assinatura.getMesesPermanenciaMinima() == null) {
            assinatura.setMesesPermanenciaMinima(3);
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

    @Transactional
    public Assinatura atualizarQuantidade(Long id, Integer novaQuantidade) {
        Assinatura assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));

        assinatura.setQuantidade(novaQuantidade);
        return assinaturaRepository.save(assinatura);
    }

    /**
     * Simula o cancelamento informando se há multa proporcional e o teto de 10%
     */
    public CancelamentoResumoDTO simularCancelamento(Long assinaturaId) {
        Assinatura assinatura = assinaturaRepository.findById(assinaturaId)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada."));

        LocalDateTime referenciaCriacao = assinatura.getDataCriacao() != null ? assinatura.getDataCriacao() : LocalDateTime.now();
        long diasDesdeCriacao = ChronoUnit.DAYS.between(referenciaCriacao, LocalDateTime.now());

        // 1. Direito de Arrependimento legal (até 7 dias) -> Isenção Total
        if (diasDesdeCriacao <= 7) {
            return CancelamentoResumoDTO.builder()
                    .isentoMulta(true)
                    .motivoIsencao("Direito de arrependimento (dentro de 7 dias corridos).")
                    .mesesCumpridos(0)
                    .mesesRestantes(0)
                    .valorMensalidade(BigDecimal.valueOf(assinatura.getValorMensal() != null ? assinatura.getValorMensal() : 0.0))
                    .saldoRestanteContrato(BigDecimal.ZERO)
                    .valorMulta(BigDecimal.ZERO)
                    .mensagem("Cancelamento gratuito garantido por lei.")
                    .build();
        }

        // 2. Calcula meses cumpridos e restantes do contrato de fidelidade (3 meses)
        long mesesDecorridos = ChronoUnit.MONTHS.between(referenciaCriacao.toLocalDate(), LocalDate.now());
        int mesesCumpridos = (int) Math.max(0, mesesDecorridos);
        int permanenciaMinima = assinatura.getMesesPermanenciaMinima() != null ? assinatura.getMesesPermanenciaMinima() : 3;
        int mesesRestantes = Math.max(0, permanenciaMinima - mesesCumpridos);

        // 3. Se já cumpriu a permanência mínima -> Isenção Total
        if (mesesRestantes == 0) {
            return CancelamentoResumoDTO.builder()
                    .isentoMulta(true)
                    .motivoIsencao("Período de permanência mínima de 3 meses cumprido.")
                    .mesesCumpridos(mesesCumpridos)
                    .mesesRestantes(0)
                    .valorMensalidade(BigDecimal.valueOf(assinatura.getValorMensal() != null ? assinatura.getValorMensal() : 0.0))
                    .saldoRestanteContrato(BigDecimal.ZERO)
                    .valorMulta(BigDecimal.ZERO)
                    .mensagem("Permanência mínima finalizada. Cancelamento livre de custos.")
                    .build();
        }

        // 4. Multa proporcional com teto de 10% sobre o saldo remanescente
        BigDecimal valorMensal = BigDecimal.valueOf(assinatura.getValorMensal() != null ? assinatura.getValorMensal() : 0.0);
        BigDecimal saldoRestante = valorMensal.multiply(BigDecimal.valueOf(mesesRestantes));
        BigDecimal valorMulta = saldoRestante.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);

        return CancelamentoResumoDTO.builder()
                .isentoMulta(false)
                .motivoIsencao(null)
                .mesesCumpridos(mesesCumpridos)
                .mesesRestantes(mesesRestantes)
                .valorMensalidade(valorMensal)
                .saldoRestanteContrato(saldoRestante)
                .valorMulta(valorMulta)
                .mensagem(String.format("Multa proporcional de 10%% aplicável sobre os %d mês(es) restantes: R$ %.2f", mesesRestantes, valorMulta))
                .build();
    }

    /**
     * Efetiva o cancelamento da assinatura e limpa o status do usuário
     */
    @Transactional
    public CancelamentoResumoDTO cancelarAssinatura(Long assinaturaId) {
        CancelamentoResumoDTO resumo = simularCancelamento(assinaturaId);

        Assinatura assinatura = assinaturaRepository.findById(assinaturaId)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada."));

        assinatura.setStatus(StatusAssinatura.CANCELADO);
        assinaturaRepository.save(assinatura);

        Usuario usuario = assinatura.getUsuario();
        if (usuario != null) {
            usuario.setPlanoAtivo(null);
            usuario.setStatusAssinatura("CANCELADA");
            usuarioRepository.save(usuario);
        }

        return resumo;
    }
}