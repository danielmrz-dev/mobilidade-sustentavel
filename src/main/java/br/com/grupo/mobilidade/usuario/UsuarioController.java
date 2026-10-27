package br.com.grupo.mobilidade.usuario;

import br.com.grupo.mobilidade.recompensa.RecompensaService;
import br.com.grupo.mobilidade.recompensa.dto.ResgateResponse;
import br.com.grupo.mobilidade.security.UsuarioAutenticado;
import br.com.grupo.mobilidade.usuario.dto.ImpactoUsuarioResponse;
import br.com.grupo.mobilidade.usuario.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Perfil, impacto ambiental e resgates do usuario logado")
public class UsuarioController {

    private final UsuarioService service;
    private final RecompensaService recompensaService;

    public UsuarioController(UsuarioService service, RecompensaService recompensaService) {
        this.service = service;
        this.recompensaService = recompensaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os usuarios (ADMIN)")
    public Page<UsuarioResponse> listar(@PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC)
                                        Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/me")
    @Operation(summary = "Dados do usuario logado, incluindo saldo de pontos")
    public UsuarioResponse perfil(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.perfil(usuario.id());
    }

    @GetMapping("/me/impacto")
    @Operation(summary = "Resumo do impacto ambiental do usuario logado")
    public ImpactoUsuarioResponse impacto(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.impacto(usuario.id());
    }

    @GetMapping("/me/resgates")
    @Operation(summary = "Historico de resgates do usuario logado")
    public Page<ResgateResponse> resgates(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                          @PageableDefault(size = 10, sort = "dataResgate", direction = Sort.Direction.DESC)
                                          Pageable pageable) {
        return recompensaService.listarResgatesDoUsuario(usuario.id(), pageable);
    }
}
