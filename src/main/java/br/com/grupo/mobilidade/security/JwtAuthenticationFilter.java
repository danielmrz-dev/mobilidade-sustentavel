package br.com.grupo.mobilidade.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Le o header "Authorization: Bearer <token>" e, se o token for valido,
 * registra o usuario no SecurityContext. Sem token valido a requisicao segue
 * anonima e o Spring Security devolve 401 nas rotas protegidas.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIXO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(PREFIXO.length()).trim();

            jwtService.validarEExtrairEmail(token).ifPresent(email -> {
                try {
                    UserDetails usuario = userDetailsService.loadUserByUsername(email);
                    var autenticacao = new UsernamePasswordAuthenticationToken(
                            usuario, null, usuario.getAuthorities());
                    autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(autenticacao);
                } catch (UsernameNotFoundException e) {
                    // Usuario do token nao existe mais: segue sem autenticar
                }
            });
        }

        chain.doFilter(request, response);
    }
}
