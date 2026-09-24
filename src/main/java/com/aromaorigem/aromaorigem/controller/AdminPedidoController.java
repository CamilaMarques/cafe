package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.MessageResponse;
import com.aromaorigem.aromaorigem.enums.StatusPedido;
import com.aromaorigem.aromaorigem.model.ItemPedido;
import com.aromaorigem.aromaorigem.model.Pedido;
import com.aromaorigem.aromaorigem.model.VarianteCafe;
import com.aromaorigem.aromaorigem.repository.PedidoRepository;
import com.aromaorigem.aromaorigem.repository.VarianteCafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pedidos")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminPedidoController {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private VarianteCafeRepository varianteCafeRepository;

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusPedido(@PathVariable Long id, @RequestParam StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        StatusPedido statusAntigo = pedido.getStatus();
        pedido.setStatus(novoStatus);

        if (novoStatus == StatusPedido.DEVOLUCAO && statusAntigo != StatusPedido.DEVOLUCAO) {
            if (pedido.getItens() != null) {
                for (ItemPedido item : pedido.getItens()) {
                    List<VarianteCafe> variantes = varianteCafeRepository.findAll();
                    for (VarianteCafe var : variantes) {
                        if (var.getPeso().equalsIgnoreCase(item.getPeso()) &&
                                var.getCafe().getNome().equalsIgnoreCase(item.getNome())) {

                            var.setEstoque(var.getEstoque() + item.getQuantidade());
                            varianteCafeRepository.save(var);
                            break;
                        }
                    }
                }
            }
        }

        pedidoRepository.save(pedido);

        return ResponseEntity.ok(new MessageResponse("Status do pedido atualizado para: " + novoStatus));
    }
}
