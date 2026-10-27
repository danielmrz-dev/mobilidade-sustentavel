package br.com.grupo.mobilidade.recompensa.dto;

import br.com.grupo.mobilidade.recompensa.Recompensa;

public record RecompensaResponse(
        Long id,
        String titulo,
        String descricao,
        Integer custoPontos,
        Integer estoque,
        boolean disponivel
) {

    public static RecompensaResponse from(Recompensa r) {
        return new RecompensaResponse(r.getId(), r.getTitulo(), r.getDescricao(), r.getCustoPontos(),
                r.getEstoque(), r.getEstoque() > 0);
    }
}
