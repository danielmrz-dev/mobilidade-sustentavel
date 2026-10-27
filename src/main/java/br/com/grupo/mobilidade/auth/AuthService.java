package br.com.grupo.mobilidade.auth;

import br.com.grupo.mobilidade.auth.dto.LoginRequest;
import br.com.grupo.mobilidade.auth.dto.RegistroRequest;
import br.com.grupo.mobilidade.auth.dto.TokenResponse;
import br.com.grupo.mobilidade.empresa.Empresa;
import br.com.grupo.mobilidade.empresa.EmpresaRepository;
import br.com.grupo.mobilidade.security.JwtService;
import br.com.grupo.mobilidade.security.UsuarioAutenticado;
import br.com.grupo.mobilidade.shared.exception.ConflitoException;
import br.com.grupo.mobilidade.shared.exception.RecursoNaoEncontradoException;
import br.com.grupo.mobilidade.usuario.Role;
import br.com.grupo.mobilidade.usuario.Usuario;
import br.com.grupo.mobilidade.usuario.UsuarioRepository;
import br.com.grupo.mobilidade.usuario.dto.UsuarioResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Cadastro publico: todo novo usuario recebe o perfil USER.
     */
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Ja existe um usuario cadastrado com o e-mail " + email);
        }

        Empresa empresa = null;
        if (request.empresaId() != null) {
            empresa = empresaRepository.findById(request.empresaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa", request.empresaId()));
        }

        var usuario = new Usuario(request.nome().trim(), email, passwordEncoder.encode(request.senha()),
                Role.USER, empresa);
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    /**
     * Valida e-mail e senha (BadCredentialsException vira 401 no GlobalExceptionHandler) e devolve o JWT.
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.senha()));

        var principal = (UsuarioAutenticado) autenticacao.getPrincipal();
        String nome = usuarioRepository.findById(principal.id()).map(Usuario::getNome).orElse(null);

        return new TokenResponse(
                jwtService.gerarToken(principal),
                "Bearer",
                jwtService.getExpiracaoMs() / 1000,
                principal.id(),
                nome,
                principal.role()
        );
    }
}
