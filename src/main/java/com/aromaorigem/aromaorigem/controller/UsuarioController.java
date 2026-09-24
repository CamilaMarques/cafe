package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.AlterarSenhaDTO;
import com.aromaorigem.aromaorigem.dto.CadastroRequest;
import com.aromaorigem.aromaorigem.dto.MessageResponse;
import com.aromaorigem.aromaorigem.dto.UsuarioResponse;
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

        // Mapeia a entidade Usuario para o DTO UsuarioResponse incluindo a role
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setSobrenome(usuario.getSobrenome());
        response.setNomeSocial(usuario.getNomeSocial());
        response.setEmail(usuario.getEmail());
        response.setRole(usuario.getRole());
        response.setCpf(usuario.getCpf());
        response.setCelular(usuario.getCelular());
        response.setDataNascimento(usuario.getDataNascimento());
        response.setDataCriacao(usuario.getDataCriacao());

        response.setCep(usuario.getCep());
        response.setRua(usuario.getRua());
        response.setNumero(usuario.getNumero());
        response.setBairro(usuario.getBairro());
        response.setCidade(usuario.getCidade());
        response.setEstado(usuario.getEstado());
        response.setComplemento(usuario.getComplemento());

        response.setCepAlternativo(usuario.getCepAlternativo());
        response.setRuaAlternativa(usuario.getRuaAlternativa());
        response.setNumeroAlternativo(usuario.getNumeroAlternativo());
        response.setBairroAlternativo(usuario.getBairroAlternativo());
        response.setCidadeAlternativa(usuario.getCidadeAlternativa());
        response.setEstadoAlternativo(usuario.getEstadoAlternativo());
        response.setComplementoAlternativo(usuario.getComplementoAlternativo());

        response.setMoagemPreferida(usuario.getMoagemPreferida());
        response.setNotasSensoriais(usuario.getNotasSensoriais());
        response.setIntensidade(usuario.getIntensidade());

        response.setStatusAssinatura(usuario.getStatusAssinatura());
        response.setCienteMudancaPlano(usuario.isCienteMudancaPlano());
        response.setPlanoAtivo(usuario.getPlanoAtivo());
        response.setDataInicioPlano(usuario.getDataInicioPlano());
        response.setContadorFidelidade(usuario.getContadorFidelidade());
        response.setContadorFidelidadeGeral(usuario.getContadorFidelidadeGeral());

        return ResponseEntity.ok(response);
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
