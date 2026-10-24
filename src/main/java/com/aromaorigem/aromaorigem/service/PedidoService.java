package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.dto.ItemPedidoDTO;
import com.aromaorigem.aromaorigem.dto.PedidoRequestDTO;
import com.aromaorigem.aromaorigem.enums.StatusPedido;
import com.aromaorigem.aromaorigem.model.*;
import com.aromaorigem.aromaorigem.repository.HistoricoStatusPedidoRepository;
import com.aromaorigem.aromaorigem.repository.PedidoRepository;
import com.aromaorigem.aromaorigem.repository.VarianteCafeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private VarianteCafeRepository varianteCafeRepository;

    @Autowired
    private HistoricoStatusPedidoRepository historicoStatusPedidoRepository;

    @Autowired
    private AssinaturaService assinaturaService;

    @Autowired
    private FidelidadeService fidelidadeService;

    @Transactional
    public Pedido criarPedido(PedidoRequestDTO dto, Usuario usuario) {
        if ("ASSINATURA".equalsIgnoreCase(dto.getTipo())) {
            if (dto.getPlano() == null || dto.getPlano().trim().isEmpty()) {
                throw new IllegalArgumentException("O plano de assinatura é obrigatório.");
            }

            Assinatura assinatura = new Assinatura();
            assinatura.setPlano(dto.getPlano());
            assinatura.setQuantidade(dto.getQuantidade() != null ? dto.getQuantidade() : 1);
            assinaturaService.salvarAssinatura(assinatura, usuario);
        }

        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .valorTotal(dto.getValorTotal())
                .valorFrete(dto.getValorFrete())
                .formaPagamento(dto.getFormaPagamento())
                .enderecoEntrega(dto.getEnderecoEntrega())
                .prazoEntrega(dto.getPrazoEntrega())
                .turnoEntrega(dto.getTurnoEntrega())
                .status(StatusPedido.PROCESSANDO)
                .build();

        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            List<ItemPedido> itensEntidade = new ArrayList<>();

            for (ItemPedidoDTO itemDto : dto.getItens()) {
                if (itemDto.getVarianteCafeId() != null) {
                    VarianteCafe variante = varianteCafeRepository.findById(itemDto.getVarianteCafeId())
                            .orElseThrow(() -> new RuntimeException("Variante do café não encontrada"));

                    if (variante.getEstoque() < itemDto.getQuantidade()) {
                        throw new RuntimeException(String.format(
                                "No momento só temos %d unidades de %s (%s) disponíveis.",
                                variante.getEstoque(), itemDto.getNome(), variante.getPeso()
                        ));
                    }

                    variante.setEstoque(variante.getEstoque() - itemDto.getQuantidade());
                    varianteCafeRepository.save(variante);
                }

                ItemPedido itemEntidade = ItemPedido.builder()
                        .pedido(pedido)
                        .nome(itemDto.getNome())
                        .peso(itemDto.getPeso())
                        .fazendaProdutora(itemDto.getFazendaProdutora())
                        .regiao(itemDto.getRegiao())
                        .altitude(itemDto.getAltitude())
                        .intensidade(itemDto.getIntensidade())
                        .precoUnitario(itemDto.getPrecoUnitario())
                        .quantidade(itemDto.getQuantidade())
                        .varianteCafeId(itemDto.getVarianteCafeId())
                        .build();

                itensEntidade.add(itemEntidade);
            }

            pedido.setItens(itensEntidade);
        }

        Pedido novoPedido = pedidoRepository.save(pedido);

        HistoricoStatusPedido historico = HistoricoStatusPedido.builder()
                .pedido(novoPedido)
                .status(novoPedido.getStatus())
                .responsavel(usuario.getEmail())
                .dataAlteracao(LocalDateTime.now())
                .build();
        historicoStatusPedidoRepository.save(historico);

        try {
            fidelidadeService.registrarEntregaFidelidade(usuario, novoPedido.getId(), "COMPRA_AVULSA");
        } catch (Exception e) {
        }

        return novoPedido;
    }

    @Transactional
    public Pedido atualizarStatusAdmin(Long pedidoId, StatusPedido novoStatus, String adminEmail) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatus(novoStatus);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        HistoricoStatusPedido historico = HistoricoStatusPedido.builder()
                .pedido(pedidoSalvo)
                .status(novoStatus)
                .responsavel(adminEmail != null ? adminEmail : "Admin")
                .dataAlteracao(LocalDateTime.now())
                .build();
        historicoStatusPedidoRepository.save(historico);

        return pedidoSalvo;
    }

    @Transactional
    public Pedido processarDevolucao(Long pedidoId, boolean aceita, String justificativa, String adminEmail) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        StatusPedido statusFinal;
        if (aceita) {
            statusFinal = StatusPedido.CANCELADO;
            pedido.setMotivoRecusaDevolucao(null);

            if (pedido.getItens() != null) {
                for (ItemPedido item : pedido.getItens()) {
                    if (item.getVarianteCafeId() != null) {
                        VarianteCafe variante = varianteCafeRepository.findById(item.getVarianteCafeId())
                                .orElse(null);
                        if (variante != null) {
                            variante.setEstoque(variante.getEstoque() + item.getQuantidade());
                            varianteCafeRepository.save(variante);
                        }
                    }
                }
            }
        } else {
            statusFinal = StatusPedido.ENTREGUE;
            pedido.setMotivoRecusaDevolucao(justificativa);
        }

        pedido.setStatus(statusFinal);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        HistoricoStatusPedido historico = HistoricoStatusPedido.builder()
                .pedido(pedidoSalvo)
                .status(statusFinal)
                .responsavel(adminEmail != null ? adminEmail : "Admin")
                .dataAlteracao(LocalDateTime.now())
                .build();
        historicoStatusPedidoRepository.save(historico);

        return pedidoSalvo;
    }

}