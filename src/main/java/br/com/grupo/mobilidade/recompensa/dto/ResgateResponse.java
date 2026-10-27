package br.com.grupo.mobilidade.recompensa.dto;

import br.com.grupo.mobilidade.recompensa.Resgate;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResgateResponse(
        Long id,
        Long recompensaId,
        String recompensa,
        Integer pontosGastos,
        LocalDateTime dataResgate,
        Integer saldoRestante
) {

    public static ResgateResponse from(Resgate resgate, Integer saldoRestante) {
        return new ResgateResponse(resgate.getId(), resgate.getRecompensa().getId(),
                resgate.getRecompensa().getTitulo(), resgate.getPontosGastos(),
                resgate.getDataResgate(), saldoRestante);
    }
}
