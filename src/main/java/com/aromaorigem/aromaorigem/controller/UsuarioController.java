package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.AlterarSenhaDTO;
import com.aromaorigem.aromaorigem.dto.CadastroRequest;
import com.aromaorigem.aromaorigem.dto.MessageResponse;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        usuario.setBairro(request.bairro());
        usuario.setCidade(request.cidade());
        usuario.setEstado(request.estado());
        usuario.setComplemento(request.complemento());

        usuario.setCepAlternativo(request.cepAlternativo());
        usuario.setRuaAlternativa(request.ruaAlternativo());
        usuario.setNumeroAlternativo(request.numeroAlternativo());
        usuario.setBairroAlternativo(request.bairroAlternativo());
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

    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(@RequestBody @Valid AlterarSenhaDTO dto, Principal principal) {

        String email = principal.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            return ResponseEntity.badRequest().body("A senha atual está incorreta.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Senha alterada com sucesso!");
    }
}
