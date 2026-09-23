package com.aromaorigem.aromaorigem.service;

import com.aromaorigem.aromaorigem.model.PedidoFidelidadeHistorico;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.PedidoFidelidadeHistoricoRepository;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class FidelidadeService {

    @Autowired
    private PedidoFidelidadeHistoricoRepository fidelidadeHistoricoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Map<String, Object> obterProgressoFidelidade(Usuario usuario) {
        long totalFeitas = fidelidadeHistoricoRepository.countByUsuario(usuario);
        int meta = 10;
        int feitas = (int) Math.min(totalFeitas, meta);
        int restante = Math.max(0, meta - feitas);
        double porcentagem = ((double) feitas / meta) * 100.0;

        Map<String, Object> progresso = new HashMap<>();
        progresso.put("feitas", feitas);
        progresso.put("meta", meta);
        progresso.put("restante", restante);
        progresso.put("porcentagem", porcentagem);
        progresso.put("historico", fidelidadeHistoricoRepository.findByUsuario(usuario));

        return progresso;
    }

    @Transactional
    public void registrarEntregaFidelidade(Usuario usuario, Long pedidoId, String tipoOrigem) {
        PedidoFidelidadeHistorico historico = new PedidoFidelidadeHistorico();
        historico.setUsuario(usuario);
        historico.setPedidoId(pedidoId);
        historico.setTipoOrigem(tipoOrigem);
        historico.setDataEntrega(LocalDate.now());

        fidelidadeHistoricoRepository.save(historico);

        long total = fidelidadeHistoricoRepository.countByUsuario(usuario);
        usuario.setContadorFidelidade((int) total);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void removerEntregaFidelidade(Long pedidoId) {
        fidelidadeHistoricoRepository.findByPedidoId(pedidoId).ifPresent(historico -> {
            Usuario usuario = historico.getUsuario();
            fidelidadeHistoricoRepository.delete(historico);

            long total = fidelidadeHistoricoRepository.countByUsuario(usuario);
            usuario.setContadorFidelidade((int) total);
            usuarioRepository.save(usuario);
        });
    }
}