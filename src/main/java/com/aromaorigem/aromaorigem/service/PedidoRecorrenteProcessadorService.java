package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.enums.StatusPedido;
import com.aromaorigem.aromaorigem.enums.TipoPlano;
import com.aromaorigem.aromaorigem.model.ItemPedido;
import com.aromaorigem.aromaorigem.model.Pedido;
import com.aromaorigem.aromaorigem.model.ProdutoRecorrente;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.PedidoRepository;
import com.aromaorigem.aromaorigem.repository.ProdutoRecorrenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoRecorrenteProcessadorService {
    @Autowired
    private ProdutoRecorrenteRepository produtoRecorrenteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private static final BigDecimal VALOR_FRETE_NORMAL = new BigDecimal("20.00");

    @Transactional
    public void processarPedidosRecorrentesDiario() {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAlvo = hoje.plusDays(7);

        List<ProdutoRecorrente> recorrentesParaProcessar = produtoRecorrenteRepository.findAll().stream()
                .filter(p -> "ATIVO".equalsIgnoreCase(p.getStatus()) && dataAlvo.equals(p.getDataProximaEntrega()))
                .toList();

        var porUsuario = recorrentesParaProcessar.stream()
                .collect(java.util.stream.Collectors.groupingBy(ProdutoRecorrente::getUsuario));

        for (var entry : porUsuario.entrySet()) {
            Usuario usuario = entry.getKey();
            List<ProdutoRecorrente> itensUsuario = entry.getValue();

            if (usuario == null || itensUsuario.isEmpty()) continue;

            BigDecimal subtotalProdutos = itensUsuario.stream()
                    .map(item -> BigDecimal.valueOf(item.getPreco()).multiply(BigDecimal.valueOf(item.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal valorFreteCalculado = BigDecimal.ZERO;
            String formaEnvio = "Normal";

            TipoPlano planoAtivo = usuario.getPlanoAtivo();
            boolean ehSommelier = planoAtivo != null && planoAtivo.name().contains("SOMMELIER");

            if (ehSommelier) {
                valorFreteCalculado = BigDecimal.ZERO;
                formaEnvio = "Expresso (Plano Sommelier - Grátis)";
            } else {
                if (subtotalProdutos.compareTo(new BigDecimal("150.00")) > 0) {
                    valorFreteCalculado = BigDecimal.ZERO;
                    formaEnvio = "Normal (Frete Grátis por valor > R$ 150)";
                } else {
                    valorFreteCalculado = VALOR_FRETE_NORMAL.divide(new BigDecimal("2.0"), 2, RoundingMode.HALF_UP);
                    formaEnvio = "Normal (50% de desconto - Pedido <= R$ 150)";
                }
            }

            BigDecimal valorTotalPedido = subtotalProdutos.add(valorFreteCalculado);

            String enderecoFormatado = "Endereço não informado";
            if (usuario.getRua() != null) {
                enderecoFormatado = String.format("%s, %s - %s, %s/%s - CEP: %s",
                        usuario.getRua(),
                        usuario.getNumero() != null ? usuario.getNumero() : "S/N",
                        usuario.getBairro() != null ? usuario.getBairro() : "",
                        usuario.getCidade() != null ? usuario.getCidade() : "",
                        usuario.getEstado() != null ? usuario.getEstado() : "",
                        usuario.getCep() != null ? usuario.getCep() : ""
                );
            }

            Pedido novoPedido = Pedido.builder()
                    .usuario(usuario)
                    .valorTotal(valorTotalPedido)
                    .valorFrete(valorFreteCalculado)
                    .formaPagamento("Cobrança Recorrente / Assinatura")
                    .enderecoEntrega(enderecoFormatado)
                    .prazoEntrega(formaEnvio)
                    .turnoEntrega("Comercial")
                    .status(StatusPedido.EM_PREPARACAO)
                    .dataCriacao(LocalDateTime.now())
                    .build();

            List<ItemPedido> itensEntidade = new ArrayList<>();
            for (ProdutoRecorrente pr : itensUsuario) {
                ItemPedido itemEntidade = ItemPedido.builder()
                        .pedido(novoPedido)
                        .nome(pr.getNome())
                        .peso(pr.getVariante() != null ? pr.getVariante().getPeso() : "250g")
                        .fazendaProdutora(pr.getCafe() != null ? pr.getCafe().getFazendaProdutora() : "Aroma & Origem")
                        .regiao(pr.getCafe() != null ? pr.getCafe().getRegiao() : "Especial")
                        .precoUnitario(BigDecimal.valueOf(pr.getPreco()))
                        .quantidade(pr.getQuantidade())
                        .varianteCafeId(pr.getVariante() != null ? pr.getVariante().getId() : null)
                        .build();
                itensEntidade.add(itemEntidade);
            }
            novoPedido.setItens(itensEntidade);

            pedidoRepository.save(novoPedido);

            for (ProdutoRecorrente pr : itensUsuario) {
                pr.setDataProximaEntrega(pr.getDataProximaEntrega().plusMonths(1));
                produtoRecorrenteRepository.save(pr);
            }
        }
    }
}