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
        if (usuarioLogado == null) {
            throw new RuntimeException("Usuário não autenticado para criar assinatura.");
        }

        // Se o usuário não escolheu um plano válido, não faz nada e retorna nulo
        if (assinatura == null || assinatura.getPlano() == null || assinatura.getPlano().trim().isEmpty() || assinatura.getPlano().equalsIgnoreCase("NENHUM")) {
            return null;
        }

        String nomeAssinatura = assinatura.getPlano().toLowerCase();
        String novoPlano = "Explorador";
        TipoPlano planoEnum = TipoPlano.EXPLORADOR;
        BigDecimal novoValorMensal = new BigDecimal("59.00");

        if (nomeAssinatura.contains("sommelier")) {
            novoPlano = "Sommelier";
            planoEnum = TipoPlano.SOMMELIER;
            novoValorMensal = new BigDecimal("189.00");
        } else if (nomeAssinatura.contains("aibiliver")) {
            novoPlano = "Aibiliver";
            planoEnum = TipoPlano.AIBILIVER;
            novoValorMensal = new BigDecimal("109.00");
        }

        assinatura.setValorMensal(novoValorMensal.doubleValue());
        assinatura.setPlano(novoPlano);
        assinatura.setUsuario(usuarioLogado);
        assinatura.setStatus(StatusAssinatura.ATIVO);

        if (assinatura.getDataCriacao() == null) {
            assinatura.setDataCriacao(LocalDateTime.now());
        }
        if (assinatura.getDataProximaEntrega() == null) {
            assinatura.setDataProximaEntrega(LocalDate.now().plusMonths(1));
        }
        if (assinatura.getMesesPermanenciaMinima() == null) {
            assinatura.setMesesPermanenciaMinima(3);
        }

        String planoAtualStr = usuarioLogado.getPlanoAtivo() != null ? usuarioLogado.getPlanoAtivo().name() : "";
        boolean ehMudancaDePlano = usuarioLogado.getPlanoAtivo() != null &&
                !planoAtualStr.equalsIgnoreCase(planoEnum.name());

        if (ehMudancaDePlano && (planoAtualStr.contains("SOMMELIER") || planoEnum == TipoPlano.SOMMELIER)) {
            assinatura.setCicloReiniciadoPorUpgrade(true);
            usuarioLogado.setContadorFidelidade(0);
        }

        usuarioLogado.setPlanoAtivo(planoEnum);
        usuarioLogado.setStatusAssinatura("ATIVA");
        if (usuarioLogado.getDataInicioPlano() == null) {
            usuarioLogado.setDataInicioPlano(LocalDate.now());
        }
        usuarioRepository.save(usuarioLogado);

        Assinatura novaAssinatura = assinaturaRepository.save(assinatura);

        if (assinaturaProducer != null) {
            assinaturaProducer.enviarEventoAssinatura(novaAssinatura);
        }

        return novaAssinatura;
    }

    @Transactional
    public void deletarAssinatura(Long id, Usuario usuarioLogado) {
        Assinatura assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));

        validarProprietario(assinatura, usuarioLogado);
        assinaturaRepository.delete(assinatura);

        limparPlanoUsuarioSeNecessario(assinatura.getUsuario(), assinatura);
    }

    @Transactional
    public Assinatura atualizarQuantidade(Long id, Integer novaQuantidade) {
        Assinatura assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));

        assinatura.setQuantidade(novaQuantidade);
        return assinaturaRepository.save(assinatura);
    }

    /**
     * Simula o cancelamento considerando:
     * - O valor mensal específico de cada plano/assinatura para o cálculo da multa.
     * - Validação correta da regra de arrependimento (7 dias) somada ao envio de produto perecível (café).
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

        BigDecimal valorMensal = BigDecimal.valueOf(assinatura.getValorMensal() != null ? assinatura.getValorMensal() : 0.0)
                .setScale(2, RoundingMode.HALF_UP);

        long mesesDecorridos = ChronoUnit.MONTHS.between(referenciaCriacao.toLocalDate(), LocalDate.now());
        int mesesCumpridos = (int) Math.max(0, mesesDecorridos);
        int permanenciaMinima = assinatura.getMesesPermanenciaMinima() != null ? assinatura.getMesesPermanenciaMinima() : 3;
        int mesesRestantes = Math.max(0, permanenciaMinima - mesesCumpridos);

        if (diasDesdeCriacao <= 7 && !produtoJaEnviado) {
            return CancelamentoResumoDTO.builder()
                    .isentoMulta(true)
                    .motivoIsencao("Direito de arrependimento (dentro de 7 dias corridos e produto não enviado).")
                    .mesesCumpridos(0)
                    .mesesRestantes(0)
                    .valorMensalidade(valorMensal)
                    .saldoRestanteContrato(BigDecimal.ZERO.setScale(2))
                    .valorMulta(BigDecimal.ZERO.setScale(2))
                    .produtoJaEnviado(false)
                    .mensagem("Cancelamento gratuito garantido por lei.")
                    .build();
        }

        if (mesesRestantes == 0) {
            return CancelamentoResumoDTO.builder()
                    .isentoMulta(true)
                    .motivoIsencao("Período de permanência mínima de 3 meses cumprido.")
                    .mesesCumpridos(mesesCumpridos)
                    .mesesRestantes(0)
                    .valorMensalidade(valorMensal)
                    .saldoRestanteContrato(BigDecimal.ZERO.setScale(2))
                    .valorMulta(BigDecimal.ZERO.setScale(2))
                    .produtoJaEnviado(produtoJaEnviado)
                    .mensagem("Permanência mínima finalizada. Cancelamento livre de custos.")
                    .build();
        }

        BigDecimal saldoRestante = valorMensal.multiply(BigDecimal.valueOf(mesesRestantes));
        BigDecimal valorMulta = saldoRestante.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);

        String avisoEnvio = produtoJaEnviado ? " (Produto já despachado/enviado)." : "";

        return CancelamentoResumoDTO.builder()
                .isentoMulta(false)
                .motivoIsencao(null)
                .mesesCumpridos(mesesCumpridos)
                .mesesRestantes(mesesRestantes)
                .valorMensalidade(valorMensal)
                .saldoRestanteContrato(saldoRestante)
                .valorMulta(valorMulta)
                .produtoJaEnviado(produtoJaEnviado)
                .mensagem(String.format("Multa proporcional de 10%% aplicável sobre os %d mês(es) restantes: R$ %.2f%s", mesesRestantes, valorMulta, avisoEnvio))
                .build();
    }

    @Transactional
    public CancelamentoResumoDTO cancelarAssinatura(Long assinaturaId, Usuario usuarioLogado) {
        CancelamentoResumoDTO resumo = simularCancelamento(assinaturaId);

        Assinatura assinatura = assinaturaRepository.findById(assinaturaId)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada."));

        validarProprietario(assinatura, usuarioLogado);
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

    private void validarProprietario(Assinatura assinatura, Usuario usuarioLogado) {
        if (usuarioLogado == null || assinatura.getUsuario() == null ||
                !assinatura.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Acesso negado");
        }
    }

    private void limparPlanoUsuarioSeNecessario(Usuario usuario, Assinatura assinaturaRemovida) {
        if (usuario == null) {
            return;
        }

        boolean mesmoPlano = usuario.getPlanoAtivo() != null &&
                usuario.getPlanoAtivo().name().equalsIgnoreCase(assinaturaRemovida.getPlano());
        boolean aindaPossuiAtiva = assinaturaRepository.findByUsuario(usuario).stream()
                .anyMatch(outra -> outra.getId() != null &&
                        !outra.getId().equals(assinaturaRemovida.getId()) &&
                        outra.getStatus() == StatusAssinatura.ATIVO);

        if (mesmoPlano && !aindaPossuiAtiva) {
            usuario.setPlanoAtivo(null);
            usuario.setStatusAssinatura("CANCELADA");
            usuarioRepository.save(usuario);
        }
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