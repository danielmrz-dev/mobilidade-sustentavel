package br.com.grupo.mobilidade.deslocamento.consulta;

import java.math.BigDecimal;

public record RankingUsuario(
        Long usuarioId,
        String nome,
        Long totalDeslocamentos,
        BigDecimal co2EvitadoKg
) {
}
