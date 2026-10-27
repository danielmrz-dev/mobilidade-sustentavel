package br.com.grupo.mobilidade.auth.dto;

import br.com.grupo.mobilidade.usuario.Role;

public record TokenResponse(
        String token,
        String tipo,
        long expiraEmSegundos,
        Long usuarioId,
        String nome,
        Role role
) {
}
