package br.com.grupo.mobilidade.modal;

import br.com.grupo.mobilidade.modal.dto.ModalRequest;
import br.com.grupo.mobilidade.modal.dto.ModalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/modais")
@Tag(name = "Modais de transporte", description = "Meios de transporte e seus fatores de emissao")
public class ModalTransporteController {

    private final ModalTransporteService service;

    public ModalTransporteController(ModalTransporteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista os modais (filtro opcional por status)")
    public Page<ModalResponse> listar(@RequestParam(required = false) StatusModal status,
                                      @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(status, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um modal pelo id")
    public ModalResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um modal (ADMIN)")
    public ResponseEntity<ModalResponse> criar(@RequestBody @Valid ModalRequest request) {
        ModalResponse criado = service.criar(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um modal (ADMIN)")
    public ModalResponse atualizar(@PathVariable Long id, @RequestBody @Valid ModalRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativa um modal - exclusao logica (ADMIN)")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
