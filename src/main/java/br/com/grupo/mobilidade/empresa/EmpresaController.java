package br.com.grupo.mobilidade.empresa;

import br.com.grupo.mobilidade.empresa.dto.EmpresaRequest;
import br.com.grupo.mobilidade.empresa.dto.EmpresaResponse;
import br.com.grupo.mobilidade.empresa.dto.RankingEmpresaResponse;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/empresas")
@Tag(name = "Empresas", description = "Empresas participantes, metas de CO2 e ranking de colaboradores")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista as empresas cadastradas")
    public Page<EmpresaResponse> listar(@PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC)
                                        Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma empresa pelo id")
    public EmpresaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/ranking")
    @Operation(summary = "Progresso da meta mensal de CO2 evitado e ranking dos colaboradores")
    public RankingEmpresaResponse ranking(@PathVariable Long id) {
        return service.ranking(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma empresa (ADMIN)")
    public ResponseEntity<EmpresaResponse> criar(@RequestBody @Valid EmpresaRequest request) {
        EmpresaResponse criada = service.criar(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma empresa (ADMIN)")
    public EmpresaResponse atualizar(@PathVariable Long id, @RequestBody @Valid EmpresaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma empresa sem usuarios vinculados (ADMIN)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
