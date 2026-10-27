package br.com.grupo.mobilidade.indicador;

import br.com.grupo.mobilidade.indicador.dto.IndicadoresResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indicadores")
@Tag(name = "Indicadores", description = "Indicadores gerais de sustentabilidade da plataforma")
public class IndicadorController {

    private final IndicadorService service;

    public IndicadorController(IndicadorService service) {
        this.service = service;
    }

    @GetMapping("/impacto")
    @Operation(summary = "CO2 evitado total, por modal e ranking das empresas")
    public IndicadoresResponse impacto() {
        return service.impactoGeral();
    }
}
