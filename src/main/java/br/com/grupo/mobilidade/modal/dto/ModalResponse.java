package br.com.grupo.mobilidade.modal.dto;

import br.com.grupo.mobilidade.modal.ModalTransporte;
import br.com.grupo.mobilidade.modal.StatusModal;
import br.com.grupo.mobilidade.modal.TipoModal;

import java.math.BigDecimal;

public record ModalResponse(
        Long id,
        String nome,
        TipoModal tipo,
        BigDecimal fatorEmissaoGKm,
        StatusModal status
) {

    public static ModalResponse from(ModalTransporte modal) {
        return new ModalResponse(modal.getId(), modal.getNome(), modal.getTipo(),
                modal.getFatorEmissaoGKm(), modal.getStatus());
    }
}
