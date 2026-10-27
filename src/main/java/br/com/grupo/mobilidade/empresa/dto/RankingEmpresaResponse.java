package br.com.grupo.mobilidade.empresa.dto;

import br.com.grupo.mobilidade.deslocamento.consulta.RankingUsuario;

import java.math.BigDecimal;
import java.util.List;

/**
 * Progresso da empresa no mes atual em relacao a meta de CO2 evitado + ranking dos colaboradores.
 */
public record RankingEmpresaResponse(
        Long empresaId,
        String empresa,
        String mesReferencia,
        BigDecimal metaCo2MensalKg,
        BigDecimal co2EvitadoNoMesKg,
        BigDecimal percentualDaMeta,
        boolean metaAtingida,
        List<RankingUsuario> ranking
) {
}
