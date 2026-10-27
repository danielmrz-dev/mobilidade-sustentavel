package br.com.grupo.mobilidade.modal.dto;

import br.com.grupo.mobilidade.modal.TipoModal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ModalRequest(

        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 60, message = "O nome deve ter no maximo 60 caracteres")
        String nome,

        @NotNull(message = "O tipo e obrigatorio")
        TipoModal tipo,

        @NotNull(message = "O fator de emissao e obrigatorio")
        @DecimalMin(value = "0.0", message = "O fator de emissao nao pode ser negativo")
        @DecimalMax(value = "999999.99", message = "Fator de emissao acima do permitido")
        @Digits(integer = 6, fraction = 2, message = "Use no maximo 6 digitos inteiros e 2 decimais")
        BigDecimal fatorEmissaoGKm
) {
}
