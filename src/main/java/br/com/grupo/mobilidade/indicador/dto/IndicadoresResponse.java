package br.com.grupo.mobilidade.indicador.dto;

import br.com.grupo.mobilidade.deslocamento.consulta.ImpactoPorModal;
import br.com.grupo.mobilidade.deslocamento.consulta.RankingEmpresa;

import java.math.BigDecimal;
import java.util.List;

/**
 * Painel geral de sustentabilidade da plataforma.
 */
public record IndicadoresResponse(
        long totalUsuarios,
        long totalDeslocamentos,
        BigDecimal distanciaTotalKm,
        BigDecimal co2EmitidoKg,
        BigDecimal co2EvitadoKg,
        BigDecimal percentualReducao,
        List<ImpactoPorModal> impactoPorModal,
        List<RankingEmpresa> rankingEmpresas
) {
}
