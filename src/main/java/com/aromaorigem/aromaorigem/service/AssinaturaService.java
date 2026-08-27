package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.dto.CancelamentoResumoDTO;
import com.aromaorigem.aromaorigem.dto.MesJornadaDTO;
import com.aromaorigem.aromaorigem.dto.ResumoClubeDTO;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

            String planoAtualStr = usuarioLogado.getPlanoAtivo() != null ? usuarioLogado.getPlanoAtivo().name() : "";

            boolean ehMudancaDePlano = usuarioLogado.getPlanoAtivo() != null &&
                    usuarioLogado.getPlanoAtivo() != TipoPlano.NENHUM &&
                    !planoAtualStr.equalsIgnoreCase(novoPlano.toUpperCase().replace(" ", "_").replace("(", "").replace(")", ""));

            boolean eraSommelier = planoAtualStr.contains("SOMMELIER");
            boolean vaiParaSommelier = novoPlano.toUpperCase().contains("SOMMELIER");
            boolean deveReiniciarCiclo = ehMudancaDePlano && (eraSommelier || vaiParaSommelier);

            if (deveReiniciarCiclo) {
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

        boolean produtoJaEnviado = false;
        if (assinatura.getDataProximaEntrega() != null) {
            LocalDateTime dataEntregaHora = assinatura.getDataProximaEntrega().atTime(12, 0);
            long horasAteEntrega = ChronoUnit.HOURS.between(LocalDateTime.now(), dataEntregaHora);

            String planoNome = assinatura.getPlano() != null ? assinatura.getPlano().toUpperCase() : "";


            if (planoNome.contains("SOMMELIER")) {
                if (horasAteEntrega <= 12 && horasAteEntrega >= 0) {
                    produtoJaEnviado = true;
                }
            } else {

                long diasAteEntrega = ChronoUnit.DAYS.between(LocalDate.now(), assinatura.getDataProximaEntrega());
                if (diasAteEntrega <= 5 && diasAteEntrega >= 0) {
                    produtoJaEnviado = true;
                }
            }
        }

        if (diasDesdeCriacao <= 7) {
            return CancelamentoResumoDTO.builder()
                    .isentoMulta(true)
                    .motivoIsencao("Direito de arrependimento (dentro de 7 dias corridos).")
                    .mesesCumpridos(0)
                    .mesesRestantes(0)
                    .valorMensalidade(BigDecimal.valueOf(assinatura.getValorMensal() != null ? assinatura.getValorMensal() : 0.0))
                    .saldoRestanteContrato(BigDecimal.ZERO)
                    .valorMulta(BigDecimal.ZERO)
                    .produtoJaEnviado(produtoJaEnviado)
                    .mensagem("Cancelamento gratuito garantido por lei.")
                    .build();
        }

        long mesesDecorridos = ChronoUnit.MONTHS.between(referenciaCriacao.toLocalDate(), LocalDate.now());
        int mesesCumpridos = (int) Math.max(0, mesesDecorridos);
        int permanenciaMinima = assinatura.getMesesPermanenciaMinima() != null ? assinatura.getMesesPermanenciaMinima() : 3;
        int mesesRestantes = Math.max(0, permanenciaMinima - mesesCumpridos);

        if (mesesRestantes == 0) {
            return CancelamentoResumoDTO.builder()
                    .isentoMulta(true)
                    .motivoIsencao("Período de permanência mínima de 3 meses cumprido.")
                    .mesesCumpridos(mesesCumpridos)
                    .mesesRestantes(0)
                    .valorMensalidade(BigDecimal.valueOf(assinatura.getValorMensal() != null ? assinatura.getValorMensal() : 0.0))
                    .saldoRestanteContrato(BigDecimal.ZERO)
                    .valorMulta(BigDecimal.ZERO)
                    .produtoJaEnviado(produtoJaEnviado)
                    .mensagem("Permanência mínima finalizada. Cancelamento livre de custos.")
                    .build();
        }

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
                .produtoJaEnviado(produtoJaEnviado)
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

    public List<MesJornadaDTO> obterJornadaAssinatura(LocalDate dataInicioPlano, String nomePlano) {
        if (dataInicioPlano == null) {
            dataInicioPlano = LocalDate.now();
        }

        LocalDate hoje = LocalDate.now();
        List<MesJornadaDTO> jornada = new ArrayList<>();

        String plano = nomePlano != null ? nomePlano.toUpperCase() : "EXPLORADOR";

        for (int i = 1; i <= 12; i++) {
            LocalDate mesReferencia = dataInicioPlano.plusMonths(i - 1);

            String status = "PENDENTE";
            if (mesReferencia.isBefore(hoje.withDayOfMonth(1))) {
                status = "CONCLUIDO";
            } else if (mesReferencia.getMonth() == hoje.getMonth() && mesReferencia.getYear() == hoje.getYear()) {
                status = "ATUAL";
            }

            String titulo = "Mês " + i + ": Curadoria Mensal de Microlotes";
            String descricao = "Pacote(s) de café especial selecionado(s) com torra artesanal.";

            if (i == 1) {
                titulo = "1ª Caixa: Estreia no Clube";
                descricao = "Microlotes selecionados + Torra artesanal gratuita.";
            } else if (plano.contains("SOMMELIER")) {
                if (i % 6 == 0) {
                    titulo = "Caixa Especial: Acessório de Barista 🌟 + Brinde Utilitário 🎁";
                    descricao = "Inclui acessório exclusivo de barista + Brinde utilitário do ciclo trimestral!";
                } else if (i % 3 == 0) {
                    titulo = "Caixa Especial: Brinde Utilitário 🎁";
                    descricao = "Inclui brinde utilitário exclusivo para o seu ritual.";
                }
            } else if (plano.contains("AIBILIVER") || plano.contains("CONECTADO")) {
                if (i % 3 == 0) {
                    titulo = "Caixa Especial: Brinde Utilitário 🎁";
                    descricao = "Inclui brinde utilitário exclusivo a cada 3 meses de assinatura.";
                }
            }

            String mesAnoFormatado = mesReferencia.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            jornada.add(new MesJornadaDTO(i, mesAnoFormatado, status, titulo, descricao));
        }

        return jornada;
    }

    public ResumoClubeDTO obterResumoClubeUsuario(Usuario usuario) {

        Assinatura assinaturaAtiva = assinaturaRepository.findFirstByUsuarioAndStatusOrderByDataCriacaoDesc(usuario, StatusAssinatura.ATIVO)
                .orElse(null);

        LocalDate inicio;
        if (assinaturaAtiva != null && assinaturaAtiva.getDataCriacao() != null) {
            inicio = assinaturaAtiva.getDataCriacao().toLocalDate();
        } else if (usuario.getDataInicioPlano() != null) {
            inicio = usuario.getDataInicioPlano();
        } else {
            inicio = LocalDate.now();
        }

        LocalDate cobranca = inicio.plusMonths(1);
        LocalDate envio = cobranca.plusDays(3);
        LocalDate fidelidade = inicio.plusMonths(3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String planoStr = usuario.getPlanoAtivo() != null ? usuario.getPlanoAtivo().name() : "EXPLORADOR";

        List<MesJornadaDTO> jornada = obterJornadaAssinatura(inicio, planoStr);

        ResumoClubeDTO resumo = new ResumoClubeDTO();
        resumo.setPlanoAtivo(usuario.getPlanoAtivo() != null ? usuario.getPlanoAtivo().toString() : null);
        resumo.setDataInicio(inicio.format(formatter));
        resumo.setProximaCobranca(cobranca.format(formatter));
        resumo.setProximoEnvio(envio.format(formatter));
        resumo.setTerminoFidelidade(fidelidade.format(formatter));
        resumo.setJornada(jornada);

        return resumo;
    }

}