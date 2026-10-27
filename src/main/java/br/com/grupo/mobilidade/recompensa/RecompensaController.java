package br.com.grupo.mobilidade.recompensa;

import br.com.grupo.mobilidade.recompensa.dto.RecompensaRequest;
import br.com.grupo.mobilidade.recompensa.dto.RecompensaResponse;
import br.com.grupo.mobilidade.recompensa.dto.ResgateResponse;
import br.com.grupo.mobilidade.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/recompensas")
@Tag(name = "Recompensas", description = "Catalogo de recompensas e troca de pontos")
public class RecompensaController {

    private final RecompensaService service;

    public RecompensaController(RecompensaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista o catalogo de recompensas")
    public Page<RecompensaResponse> listar(@RequestParam(defaultValue = "false") boolean disponiveis,
                                           @PageableDefault(size = 20, sort = "custoPontos", direction = Sort.Direction.ASC)
                                           Pageable pageable) {
        return service.listar(disponiveis, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma recompensa pelo id")
    public RecompensaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma recompensa (ADMIN)")
    public ResponseEntity<RecompensaResponse> criar(@RequestBody @Valid RecompensaRequest request) {
        RecompensaResponse criada = service.criar(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma recompensa (ADMIN)")
    public RecompensaResponse atualizar(@PathVariable Long id, @RequestBody @Valid RecompensaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma recompensa que nunca foi resgatada (ADMIN)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resgates")
    @Operation(summary = "Troca pontos do usuario logado pela recompensa")
    public ResponseEntity<ResgateResponse> resgatar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                    @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.resgatar(usuario.id(), id));
    }
}
