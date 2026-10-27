package br.com.grupo.mobilidade.indicador;

import br.com.grupo.mobilidade.deslocamento.DeslocamentoRepository;
import br.com.grupo.mobilidade.deslocamento.consulta.ResumoImpacto;
import br.com.grupo.mobilidade.indicador.dto.IndicadoresResponse;
import br.com.grupo.mobilidade.usuario.UsuarioRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultas agregadas de leitura. Nao tem entidade propria: reaproveita os dados de deslocamentos.
 */
@Service
public class IndicadorService {

    private static final int TOP_EMPRESAS = 5;

    private final DeslocamentoRepository deslocamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public IndicadorService(DeslocamentoRepository deslocamentoRepository, UsuarioRepository usuarioRepository) {
        this.deslocamentoRepository = deslocamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public IndicadoresResponse impactoGeral() {
        ResumoImpacto resumo = deslocamentoRepository.resumirGeral();
        return new IndicadoresResponse(
                usuarioRepository.count(),
                resumo.totalDeslocamentos(),
                resumo.distanciaTotalKm(),
                resumo.co2EmitidoKg(),
                resumo.co2EvitadoKg(),
                resumo.percentualReducao(),
                deslocamentoRepository.impactoPorModal(),
                deslocamentoRepository.rankingEmpresas(PageRequest.of(0, TOP_EMPRESAS))
        );
    }
}
