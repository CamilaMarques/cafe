package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.PedidoRequestDTO;
import com.aromaorigem.aromaorigem.enums.StatusPedido;
import com.aromaorigem.aromaorigem.model.Assinatura;
import com.aromaorigem.aromaorigem.model.ItemPedido;
import com.aromaorigem.aromaorigem.model.Pedido;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.PedidoRepository;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import com.aromaorigem.aromaorigem.service.AssinaturaService;
import com.aromaorigem.aromaorigem.service.FidelidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FidelidadeService fidelidadeService;

    @Autowired
    private AssinaturaService assinaturaService;

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody PedidoRequestDTO dto, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if ("ASSINATURA".equalsIgnoreCase(dto.getTipo())) {
            if (dto.getPlano() == null || dto.getPlano().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Assinatura assinatura = new Assinatura();
            assinatura.setPlano(dto.getPlano());
            assinatura.setQuantidade(dto.getQuantidade() != null ? dto.getQuantidade() : 1);
            assinaturaService.salvarAssinatura(assinatura, usuario);
        }

        // 1. Cria o pedido base
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
            List<ItemPedido> itensEntidade = dto.getItens().stream().map(itemDto ->
                    ItemPedido.builder()
                            .pedido(pedido)
                            .nome(itemDto.getNome())
                            .peso(itemDto.getPeso())
                            .fazendaProdutora(itemDto.getFazendaProdutora())
                            .regiao(itemDto.getRegiao())
                            .altitude(itemDto.getAltitude())
                            .intensidade(itemDto.getIntensidade())
                            .precoUnitario(itemDto.getPrecoUnitario())
                            .quantidade(itemDto.getQuantidade())
                            .build()
            ).toList();

            pedido.setItens(itensEntidade);
        }

        Pedido novoPedido = pedidoRepository.save(pedido);

        try {
            fidelidadeService.registrarEntregaFidelidade(usuario, novoPedido.getId(), "COMPRA_AVULSA");
        } catch (Exception e) {

        }

        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidosUsuario(Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByDataCriacaoDesc(usuario);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
        }

        if (pedido.getStatus() != StatusPedido.PENDENTE && pedido.getStatus() != StatusPedido.PROCESSANDO) {
            return ResponseEntity.badRequest().body("Este pedido não pode mais ser cancelado pois já está em andamento ou finalizado.");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);

        // Remove ou estorna a contagem da fidelidade ao cancelar o pedido
        try {
            fidelidadeService.removerEntregaFidelidade(pedido.getId());
        } catch (Exception e) {
            // Caso o método tenha outro nome no seu service, ajuste conforme a sua implementação
        }

        return ResponseEntity.ok(pedido);
    }
}
