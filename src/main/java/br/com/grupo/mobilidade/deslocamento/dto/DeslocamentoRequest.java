package br.com.grupo.mobilidade.deslocamento.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DeslocamentoRequest(

        @NotNull(message = "O modal e obrigatorio")
        @Positive(message = "Id de modal invalido")
        Long modalId,

        @NotNull(message = "A distancia e obrigatoria")
        @DecimalMin(value = "0.1", message = "A distancia minima e 0.1 km")
        @DecimalMax(value = "1000", message = "A distancia maxima por deslocamento e 1000 km")
        @Digits(integer = 4, fraction = 2, message = "Use no maximo 2 casas decimais")
        BigDecimal distanciaKm,

        @NotNull(message = "A data e obrigatoria")
        @PastOrPresent(message = "A data do deslocamento nao pode estar no futuro")
        LocalDate data,

        @Size(max = 120, message = "A origem deve ter no maximo 120 caracteres")
        String origem,

        @Size(max = 120, message = "O destino deve ter no maximo 120 caracteres")
        String destino
) {
}
