package br.com.grupo.mobilidade.security;

import br.com.grupo.mobilidade.usuario.Role;
import br.com.grupo.mobilidade.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Representa o usuario logado dentro do Spring Security.
 * Nos controllers e obtido com {@code @AuthenticationPrincipal UsuarioAutenticado usuario}.
 */
public record UsuarioAutenticado(Long id, String email, String senha, Role role) implements UserDetails {

    public static UsuarioAutenticado from(Usuario usuario) {
        return new UsuarioAutenticado(usuario.getId(), usuario.getEmail(), usuario.getSenha(), usuario.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
