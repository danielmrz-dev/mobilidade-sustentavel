package br.com.grupo.mobilidade.deslocamento.consulta;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Totais agregados de um conjunto de deslocamentos (preenchido direto pela consulta JPQL).
 */
public record ResumoImpacto(
        Long totalDeslocamentos,
        BigDecimal distanciaTotalKm,
        BigDecimal co2EmitidoKg,
        BigDecimal co2EvitadoKg
) {

    public ResumoImpacto {
        // SUM devolve null quando nao ha linhas
        totalDeslocamentos = totalDeslocamentos == null ? 0L : totalDeslocamentos;
        distanciaTotalKm = distanciaTotalKm == null ? BigDecimal.ZERO : distanciaTotalKm;
        co2EmitidoKg = co2EmitidoKg == null ? BigDecimal.ZERO : co2EmitidoKg;
        co2EvitadoKg = co2EvitadoKg == null ? BigDecimal.ZERO : co2EvitadoKg;
    }

    /**
     * Percentual de reducao em relacao ao cenario em que todos os trajetos fossem de carro a combustao.
     */
    public BigDecimal percentualReducao() {
        BigDecimal cenarioBase = co2EmitidoKg.add(co2EvitadoKg);
        if (cenarioBase.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return co2EvitadoKg.multiply(BigDecimal.valueOf(100)).divide(cenarioBase, 2, RoundingMode.HALF_UP);
    }
}
