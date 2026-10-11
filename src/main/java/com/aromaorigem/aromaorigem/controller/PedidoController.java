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
import java.time.LocalDateTime;
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
    public ResponseEntity<List<Pedido>> listarPedidosAdmin(
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String mes,
            @RequestParam(required = false) Long id,
            Principal principal) {

        if (id != null) {
            return pedidoRepository.findById(id)
                    .map(p -> ResponseEntity.ok(List.of(p)))
                    .orElse(ResponseEntity.ok(List.of()));
        }

        if (data != null && !data.isBlank()) {
            java.time.LocalDate localDate = java.time.LocalDate.parse(data);
            LocalDateTime inicio = localDate.atStartOfDay();
            LocalDateTime fim = localDate.atTime(23, 59, 59);
            return ResponseEntity.ok(pedidoRepository.findByDataCriacaoBetween(inicio, fim));
        }

        if (mes != null && !mes.isBlank()) {
            java.time.YearMonth yearMonth = java.time.YearMonth.parse(mes);
            LocalDateTime inicio = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime fim = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            return ResponseEntity.ok(pedidoRepository.findByDataCriacaoBetween(inicio, fim));
        }

        return ResponseEntity.ok(pedidoRepository.findAllByOrderByDataCriacaoDesc());
    }

    @PatchMapping("/admin/{id}/devolucao")
    public ResponseEntity<?> tratarDevolucao(
            @PathVariable Long id,
            @RequestParam boolean aceita,
            @RequestParam(required = false) String justificativa,
            Principal principal) {
        try {
            String emailAdmin = principal != null ? principal.getName() : "Admin";
            Pedido pedidoAtualizado = pedidoService.processarDevolucao(id, aceita, justificativa, emailAdmin);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<?> atualizarStatusPedido(@PathVariable Long id, @RequestParam StatusPedido novoStatus, Principal principal) {
        try {
            String emailAdmin = principal != null ? principal.getName() : "Admin";
            Pedido pedido = pedidoService.atualizarStatusAdmin(id, novoStatus, emailAdmin);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
