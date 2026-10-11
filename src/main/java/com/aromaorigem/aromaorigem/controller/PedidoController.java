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
import com.aromaorigem.aromaorigem.service.PedidoService;
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
    private PedidoService pedidoService;

    @Autowired
    private FidelidadeService fidelidadeService;

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoRequestDTO dto, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            Pedido novoPedido = pedidoService.criarPedido(dto, usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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

        try {
            fidelidadeService.removerEntregaFidelidade(pedido.getId());
        } catch (Exception e) {
        }

        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Pedido>> listarTodosPedidosAdmin(Principal principal) {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByDataCriacaoDesc();
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<?> atualizarStatusPedido(@PathVariable Long id, @RequestParam StatusPedido novoStatus, Principal principal) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);

        return ResponseEntity.ok(pedido);
    }
}
