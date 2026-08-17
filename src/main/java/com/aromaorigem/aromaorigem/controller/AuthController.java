package com.aromaorigem.aromaorigem.controller;

import com.aromaorigem.aromaorigem.dto.CadastroRequest;
import com.aromaorigem.aromaorigem.dto.LoginRequest;
import com.aromaorigem.aromaorigem.dto.MessageResponse;
import com.aromaorigem.aromaorigem.model.Usuario;
import com.aromaorigem.aromaorigem.repository.UsuarioRepository;
import com.aromaorigem.aromaorigem.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new MessageResponse("Login realizado com sucesso!"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody CadastroRequest cadastroRequest) {
        if (usuarioRepository.existsByEmail(cadastroRequest.email())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Este e-mail já possui cadastro em nosso sistema!"));
        }

        Usuario usuario = new Usuario();
        usuario.setNome(cadastroRequest.nome());
        usuario.setEmail(cadastroRequest.email());
        usuario.setSenha(encoder.encode(cadastroRequest.senha()));
        usuario.setRole("ROLE_USER");

        // Dados Pessoais Extras
        usuario.setCpf(cadastroRequest.cpf());
        usuario.setCelular(cadastroRequest.celular());
        usuario.setDataNascimento(cadastroRequest.dataNascimento());

        // Endereço Principal
        usuario.setCep(cadastroRequest.cep());
        usuario.setRua(cadastroRequest.rua());
        usuario.setNumero(cadastroRequest.numero());
        usuario.setCidade(cadastroRequest.cidade());
        usuario.setEstado(cadastroRequest.estado());
        usuario.setComplemento(cadastroRequest.complemento());

        // Endereço Alternativo
        usuario.setCepAlternativo(cadastroRequest.cepAlternativo());
        usuario.setRuaAlternativa(cadastroRequest.ruaAlternativo());
        usuario.setNumeroAlternativo(cadastroRequest.numeroAlternativo());
        usuario.setCidadeAlternativa(cadastroRequest.cidadeAlternativo());
        usuario.setEstadoAlternativo(cadastroRequest.estadoAlternativo());
        usuario.setComplementoAlternativo(cadastroRequest.complementoAlternativo());

        // Preferências
        usuario.setMoagemPreferida(cadastroRequest.moagemPreferida());
        usuario.setNotasSensoriais(cadastroRequest.notasSensoriais());
        usuario.setIntensidade(cadastroRequest.intensidade());

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new MessageResponse("Logout realizado com sucesso!"));
    }

    @PostMapping("/admin/cadastrar")
    public ResponseEntity<?> cadastrarAdmin(@RequestBody CadastroRequest cadastroRequest) {
        if (usuarioRepository.existsByEmail(cadastroRequest.email())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Este e-mail já possui cadastro em nosso sistema!"));
        }

        Usuario admin = new Usuario();
        admin.setNome(cadastroRequest.nome());
        admin.setEmail(cadastroRequest.email());
        admin.setSenha(encoder.encode(cadastroRequest.senha()));
        admin.setRole("ROLE_ADMIN");

        usuarioRepository.save(admin);

        return ResponseEntity.ok(new MessageResponse("Administrador registrado com sucesso!"));
    }
}
