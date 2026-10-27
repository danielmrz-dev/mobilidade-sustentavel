package br.com.grupo.mobilidade.deslocamento;

import java.math.BigDecimal;

/**
 * Resultado do calculo ambiental de um deslocamento.
 */
public record ResultadoImpacto(BigDecimal co2EmitidoKg, BigDecimal co2EvitadoKg, int pontos) {
}
