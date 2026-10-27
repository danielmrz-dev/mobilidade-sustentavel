package br.com.grupo.mobilidade.usuario.dto;

import br.com.grupo.mobilidade.usuario.Role;
import br.com.grupo.mobilidade.usuario.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Role role,
        Integer saldoPontos,
        Long empresaId,
        String empresaNome,
        LocalDateTime criadoEm
) {

    public static UsuarioResponse from(Usuario usuario) {
        var empresa = usuario.getEmpresa();
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getSaldoPontos(),
                empresa != null ? empresa.getId() : null,
                empresa != null ? empresa.getNome() : null,
                usuario.getCriadoEm()
        );
    }
}
