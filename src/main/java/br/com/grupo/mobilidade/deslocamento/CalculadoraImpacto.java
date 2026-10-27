package br.com.grupo.mobilidade.deslocamento;

import br.com.grupo.mobilidade.config.MobilidadeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regra central do dominio.
 *
 * <pre>
 * CO2 emitido (kg) = distancia (km) x fator do modal (g/km) / 1000
 * CO2 evitado (kg) = distancia (km) x (fator da referencia - fator do modal) / 1000   (nunca negativo)
 * Pontos           = CO2 evitado (kg) x pontos por kg   (arredondado para baixo)
 * </pre>
 *
 * A referencia e o carro a combustao: o quanto o usuario deixou de emitir por nao usa-lo.
 */
@Component
public class CalculadoraImpacto {

    private static final BigDecimal MIL = BigDecimal.valueOf(1000);
    private static final int ESCALA_KG = 3;

    private final int pontosPorKgCo2;

    @Autowired
    public CalculadoraImpacto(MobilidadeProperties properties) {
        this(properties.pontosPorKgCo2());
    }

    CalculadoraImpacto(int pontosPorKgCo2) {
        this.pontosPorKgCo2 = pontosPorKgCo2;
    }

    public ResultadoImpacto calcular(BigDecimal distanciaKm, BigDecimal fatorModalGKm, BigDecimal fatorReferenciaGKm) {
        BigDecimal emitido = distanciaKm.multiply(fatorModalGKm)
                .divide(MIL, ESCALA_KG, RoundingMode.HALF_UP);

        BigDecimal diferencaFator = fatorReferenciaGKm.subtract(fatorModalGKm).max(BigDecimal.ZERO);
        BigDecimal evitado = distanciaKm.multiply(diferencaFator)
                .divide(MIL, ESCALA_KG, RoundingMode.HALF_UP);

        int pontos = evitado.multiply(BigDecimal.valueOf(pontosPorKgCo2))
                .setScale(0, RoundingMode.DOWN)
                .intValueExact();

        return new ResultadoImpacto(emitido, evitado, pontos);
    }
}
