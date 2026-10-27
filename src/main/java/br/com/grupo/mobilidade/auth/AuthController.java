package br.com.grupo.mobilidade.auth;

import br.com.grupo.mobilidade.auth.dto.LoginRequest;
import br.com.grupo.mobilidade.auth.dto.RegistroRequest;
import br.com.grupo.mobilidade.auth.dto.TokenResponse;
import br.com.grupo.mobilidade.usuario.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacao", description = "Cadastro e login (rotas publicas)")
@SecurityRequirements // remove o cadeado do Swagger nestas rotas
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/registro")
    @Operation(summary = "Cadastra um novo usuario com perfil USER")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody @Valid RegistroRequest request) {
        UsuarioResponse criado = service.registrar(request);
        var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/usuarios/me").build().toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica e devolve o token JWT")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        return service.login(request);
    }
}
