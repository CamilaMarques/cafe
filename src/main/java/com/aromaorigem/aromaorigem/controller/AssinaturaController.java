package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.CancelamentoResumoDTO;
import com.aromaorigem.aromaorigem.dto.MesJornadaDTO;
import com.aromaorigem.aromaorigem.dto.ResumoClubeDTO;
import com.aromaorigem.aromaorigem.model.Assinatura;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import com.aromaorigem.aromaorigem.service.AssinaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assinaturas")
@CrossOrigin(origins = "*")
public class AssinaturaController {

    @Autowired
    private AssinaturaService assinaturaService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Assinatura>> listarTodas() {
        return ResponseEntity.ok(assinaturaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assinatura> buscarPorId(@PathVariable Long id) {
        return assinaturaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Assinatura> criarAssinatura(
            @RequestBody Assinatura assinatura,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Assinatura novaAssinatura = assinaturaService.salvarAssinatura(assinatura, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaAssinatura);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAssinatura(@PathVariable Long id) {
        assinaturaService.deletarAssinatura(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<Assinatura> atualizarQuantidade(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> payload) {

        Integer novaQuantidade = payload.get("quantidade");
        Assinatura assinaturaAtualizada = assinaturaService.atualizarQuantidade(id, novaQuantidade);
        return ResponseEntity.ok(assinaturaAtualizada);
    }

    /**
     * Endpoint para simular o cancelamento e verificar se há multa proporcional ou isenção (7 dias / 3 meses)
     */
    @GetMapping("/{id}/simular-cancelamento")
    public ResponseEntity<CancelamentoResumoDTO> simularCancelamento(@PathVariable Long id) {
        CancelamentoResumoDTO resumo = assinaturaService.simularCancelamento(id);
        return ResponseEntity.ok(resumo);
    }

    /**
     * Endpoint para efetivar o cancelamento da assinatura do clube
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<CancelamentoResumoDTO> cancelarAssinatura(@PathVariable Long id) {
        CancelamentoResumoDTO resultado = assinaturaService.cancelarAssinatura(id);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/jornada")
    public ResponseEntity<List<MesJornadaDTO>> getJornadaUsuario(Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        String planoString = usuario.getPlanoAtivo() != null ? usuario.getPlanoAtivo().name() : "EXPLORADOR";

        List<MesJornadaDTO> jornada = assinaturaService.obterJornadaAssinatura(
                usuario.getDataInicioPlano(),
                planoString
        );

        return ResponseEntity.ok(jornada);
    }

    @GetMapping("/resumo-clube")
    public ResponseEntity<ResumoClubeDTO> getResumoClube(Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ResumoClubeDTO resumo = assinaturaService.obterResumoClubeUsuario(usuario);
        return ResponseEntity.ok(resumo);
    }
}