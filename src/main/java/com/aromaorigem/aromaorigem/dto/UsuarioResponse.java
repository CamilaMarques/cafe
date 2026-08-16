package com.aromaorigem.aromaorigem.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(Long id, String nome, String email, String role, LocalDateTime dataCriacao) {
}
