package br.com.grupo.mobilidade.deslocamento.consulta;

import br.com.grupo.mobilidade.modal.TipoModal;

import java.math.BigDecimal;

public record ImpactoPorModal(
        String modal,
        TipoModal tipo,
        Long totalDeslocamentos,
        BigDecimal distanciaTotalKm,
        BigDecimal co2EvitadoKg
) {
}
