package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.CadastroRequest;
import com.aromaorigem.aromaorigem.dto.MessageResponse;
import com.aromaorigem.aromaorigem.dto.UsuarioResponse;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public ResponseEntity<?> obterPerfil() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getDataCriacao(),
                // Pessoais Extras
                usuario.getCpf(),
                usuario.getCelular(),
                usuario.getDataNascimento(),
                // Endereço Principal
                usuario.getCep(),
                usuario.getRua(),
                usuario.getNumero(),
                usuario.getCidade(),
                usuario.getEstado(),
                usuario.getComplemento(),
                // Endereço Alternativo
                usuario.getCepAlternativo(),
                usuario.getRuaAlternativa(),
                usuario.getNumeroAlternativo(),
                usuario.getCidadeAlternativa(),
                usuario.getEstadoAlternativo(),
                usuario.getComplementoAlternativo(),
                // Preferências
                usuario.getMoagemPreferida(),
                usuario.getNotasSensoriais(),
                usuario.getIntensidade(),
                usuario.getPlanoAtivo(),
                usuario.getStatusAssinatura(),
                usuario.getContadorFidelidade()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/perfil/editar")
    public ResponseEntity<?> atualizarPerfil(@RequestBody CadastroRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(request.nome());
        usuario.setCpf(request.cpf());
        usuario.setCelular(request.celular());
        usuario.setDataNascimento(request.dataNascimento());

        usuario.setCep(request.cep());
        usuario.setRua(request.rua());
        usuario.setNumero(request.numero());
        usuario.setCidade(request.cidade());
        usuario.setEstado(request.estado());
        usuario.setComplemento(request.complemento());

        usuario.setCepAlternativo(request.cepAlternativo());
        usuario.setRuaAlternativa(request.ruaAlternativo());
        usuario.setNumeroAlternativo(request.numeroAlternativo());
        usuario.setCidadeAlternativa(request.cidadeAlternativo());
        usuario.setEstadoAlternativo(request.estadoAlternativo());
        usuario.setComplementoAlternativo(request.complementoAlternativo());

        usuario.setMoagemPreferida(request.moagemPreferida());
        usuario.setNotasSensoriais(request.notasSensoriais());
        usuario.setIntensidade(request.intensidade());

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Perfil atualizado com sucesso!"));
    }
}
