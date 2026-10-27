package br.com.grupo.mobilidade.deslocamento.consulta;

import java.math.BigDecimal;

public record RankingEmpresa(
        Long empresaId,
        String nome,
        Long totalDeslocamentos,
        BigDecimal co2EvitadoKg
) {
}
