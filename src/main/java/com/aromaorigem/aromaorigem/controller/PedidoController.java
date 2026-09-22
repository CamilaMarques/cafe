package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.model.Pedido;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.PedidoRepository;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
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

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedidoDto, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        pedidoDto.setUsuario(usuario);

        if (pedidoDto.getStatus() == null) {
            pedidoDto.setStatus(com.aromaorigem.aromaorigem.enums.StatusPedido.EM_TRANSITO);
        }

        Pedido novoPedido = pedidoRepository.save(pedidoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidosUsuario(Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByDataCriacaoDesc(usuario);
        return ResponseEntity.ok(pedidos);
    }
}
