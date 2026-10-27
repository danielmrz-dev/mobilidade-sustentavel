package br.com.grupo.mobilidade.security;

import br.com.grupo.mobilidade.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

/**
 * Gera e valida tokens JWT assinados com HMAC-SHA.
 */
@Service
public class JwtService {

    private static final String ISSUER = "mobilidade-sustentavel-api";

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(JwtProperties properties) {
        this.chave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.expiracaoMs = properties.expirationMs();
    }

    public String gerarToken(UsuarioAutenticado usuario) {
        Date agora = new Date();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(usuario.email())
                .claim("id", usuario.id())
                .claim("role", usuario.role().name())
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)
                .compact();
    }

    /**
     * Devolve o e-mail (subject) se o token for valido; vazio se estiver expirado, adulterado ou malformado.
     */
    public Optional<String> validarEExtrairEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long getExpiracaoMs() {
        return expiracaoMs;
    }
}
