package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.CadastroRequest;
import com.aromaorigem.aromaorigem.dto.MessageResponse;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Usuário não autenticado"));
        }

        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/perfil/editar")
    public ResponseEntity<?> atualizarPerfil(@RequestBody CadastroRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Usuário não autenticado"));
        }

        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(request.nome());
        usuario.setSobrenome(request.sobrenome());
        usuario.setNomeSocial(request.nomeSocial());
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

    @PostMapping("/perfil/ciente-mudanca")
    public ResponseEntity<?> registrarCienteMudanca() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Usuário não autenticado"));
        }

        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setCienteMudancaPlano(true);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Confirmação de ciente registrada com sucesso!"));
    }
}
