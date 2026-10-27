package br.com.grupo.mobilidade.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * Parametros de negocio configuraveis por variavel de ambiente (ver application.yml).
 */
@Validated
@ConfigurationProperties(prefix = "mobilidade")
public record MobilidadeProperties(
        @Positive int pontosPorKgCo2,
        @NotNull @Positive BigDecimal fatorReferenciaPadraoGKm,
        @Valid @NotNull Admin admin
) {

    public record Admin(
            @NotBlank String nome,
            @NotBlank String email,
            @NotBlank String senha
    ) {
    }
}
