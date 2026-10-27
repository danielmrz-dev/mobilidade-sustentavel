package br.com.grupo.mobilidade.recompensa.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RecompensaRequest(

        @NotBlank(message = "O titulo e obrigatorio")
        @Size(max = 100, message = "O titulo deve ter no maximo 100 caracteres")
        String titulo,

        @Size(max = 255, message = "A descricao deve ter no maximo 255 caracteres")
        String descricao,

        @NotNull(message = "O custo em pontos e obrigatorio")
        @Positive(message = "O custo em pontos deve ser maior que zero")
        @Max(value = 1_000_000, message = "Custo em pontos acima do permitido")
        Integer custoPontos,

        @NotNull(message = "O estoque e obrigatorio")
        @PositiveOrZero(message = "O estoque nao pode ser negativo")
        @Max(value = 1_000_000, message = "Estoque acima do permitido")
        Integer estoque
) {
}
