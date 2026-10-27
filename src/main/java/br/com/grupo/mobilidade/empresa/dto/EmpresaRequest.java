package br.com.grupo.mobilidade.empresa.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

import java.math.BigDecimal;

public record EmpresaRequest(

        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
        String nome,

        @NotBlank(message = "O CNPJ e obrigatorio")
        @Pattern(regexp = "\\d{14}", message = "Informe o CNPJ apenas com os 14 digitos, sem pontuacao")
        @CNPJ(message = "CNPJ invalido (digitos verificadores nao conferem)")
        String cnpj,

        @PositiveOrZero(message = "A meta mensal nao pode ser negativa")
        @Digits(integer = 10, fraction = 2, message = "Use no maximo 10 digitos inteiros e 2 decimais")
        BigDecimal metaCo2MensalKg
) {
}
