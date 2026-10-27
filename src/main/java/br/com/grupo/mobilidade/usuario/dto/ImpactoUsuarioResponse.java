package br.com.grupo.mobilidade.usuario.dto;

import java.math.BigDecimal;

public record ImpactoUsuarioResponse(
        Long usuarioId,
        String nome,
        long totalDeslocamentos,
        BigDecimal distanciaTotalKm,
        BigDecimal co2EmitidoKg,
        BigDecimal co2EvitadoKg,
        BigDecimal percentualReducao,
        Integer saldoPontos
) {
}
