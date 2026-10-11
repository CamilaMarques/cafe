package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.dto.ItemPedidoDTO;
import com.aromaorigem.aromaorigem.dto.PedidoRequestDTO;
import com.aromaorigem.aromaorigem.enums.StatusPedido;
import com.aromaorigem.aromaorigem.model.*;
import com.aromaorigem.aromaorigem.repository.PedidoRepository;
import com.aromaorigem.aromaorigem.repository.VarianteCafeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private VarianteCafeRepository varianteCafeRepository;

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

        try {
            fidelidadeService.registrarEntregaFidelidade(usuario, novoPedido.getId(), "COMPRA_AVULSA");
        } catch (Exception e) {

        }

        return novoPedido;
    }
}