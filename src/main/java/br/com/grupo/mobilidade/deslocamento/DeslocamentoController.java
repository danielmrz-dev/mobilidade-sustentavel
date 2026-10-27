package br.com.grupo.mobilidade.deslocamento;

import br.com.grupo.mobilidade.deslocamento.dto.DeslocamentoRequest;
import br.com.grupo.mobilidade.deslocamento.dto.DeslocamentoResponse;
import br.com.grupo.mobilidade.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/deslocamentos")
@Tag(name = "Deslocamentos", description = "Registro de trajetos do usuario logado e calculo de CO2 evitado")
public class DeslocamentoController {

    private final DeslocamentoService service;

    public DeslocamentoController(DeslocamentoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registra um deslocamento, calcula o CO2 e credita pontos")
    public ResponseEntity<DeslocamentoResponse> registrar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                          @RequestBody @Valid DeslocamentoRequest request) {
        DeslocamentoResponse criado = service.registrar(usuario.id(), request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @GetMapping
    @Operation(summary = "Lista os deslocamentos do usuario logado (filtro opcional por periodo)")
    public Page<DeslocamentoResponse> listar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @PageableDefault(size = 10, sort = "dataDeslocamento", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(usuario.id(), de, ate, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha um deslocamento do usuario logado")
    public DeslocamentoResponse buscar(@AuthenticationPrincipal UsuarioAutenticado usuario, @PathVariable Long id) {
        return service.buscar(usuario.id(), id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um deslocamento e recalcula o impacto")
    public DeslocamentoResponse atualizar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                          @PathVariable Long id,
                                          @RequestBody @Valid DeslocamentoRequest request) {
        return service.atualizar(usuario.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um deslocamento e estorna os pontos")
    public ResponseEntity<Void> remover(@AuthenticationPrincipal UsuarioAutenticado usuario, @PathVariable Long id) {
        service.remover(usuario.id(), id);
        return ResponseEntity.noContent().build();
    }
}
