package br.com.grupo.mobilidade.deslocamento.dto;

import br.com.grupo.mobilidade.deslocamento.Deslocamento;
import br.com.grupo.mobilidade.modal.TipoModal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DeslocamentoResponse(
        Long id,
        Long modalId,
        String modal,
        TipoModal tipoModal,
        BigDecimal distanciaKm,
        LocalDate data,
        String origem,
        String destino,
        BigDecimal co2EmitidoKg,
        BigDecimal co2EvitadoKg,
        Integer pontosGerados
) {

    public static DeslocamentoResponse from(Deslocamento d) {
        var modal = d.getModal();
        return new DeslocamentoResponse(
                d.getId(),
                modal.getId(),
                modal.getNome(),
                modal.getTipo(),
                d.getDistanciaKm(),
                d.getDataDeslocamento(),
                d.getOrigem(),
                d.getDestino(),
                d.getCo2EmitidoKg(),
                d.getCo2EvitadoKg(),
                d.getPontosGerados()
        );
    }
}
