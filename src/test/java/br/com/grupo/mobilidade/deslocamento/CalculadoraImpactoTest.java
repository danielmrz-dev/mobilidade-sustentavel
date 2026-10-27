package br.com.grupo.mobilidade.deslocamento;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraImpactoTest {

    private static final BigDecimal REFERENCIA = new BigDecimal("150");

    private final CalculadoraImpacto calculadora = new CalculadoraImpacto(10);

    @Test
    @DisplayName("Bicicleta nao emite CO2 e evita toda a emissao do carro")
    void bicicletaEvitaTudo() {
        ResultadoImpacto r = calculadora.calcular(new BigDecimal("10"), BigDecimal.ZERO, REFERENCIA);

        assertThat(r.co2EmitidoKg()).isEqualByComparingTo("0");
        assertThat(r.co2EvitadoKg()).isEqualByComparingTo("1.5");
        assertThat(r.pontos()).isEqualTo(15);
    }

    @Test
    @DisplayName("Onibus emite parte e evita a diferenca em relacao ao carro")
    void onibusEvitaDiferenca() {
        ResultadoImpacto r = calculadora.calcular(new BigDecimal("20"), new BigDecimal("70"), REFERENCIA);

        assertThat(r.co2EmitidoKg()).isEqualByComparingTo("1.4");
        assertThat(r.co2EvitadoKg()).isEqualByComparingTo("1.6");
        assertThat(r.pontos()).isEqualTo(16);
    }

    @Test
    @DisplayName("Carro a combustao nao gera CO2 evitado nem pontos")
    void carroNaoGeraPontos() {
        ResultadoImpacto r = calculadora.calcular(new BigDecimal("15"), REFERENCIA, REFERENCIA);

        assertThat(r.co2EmitidoKg()).isEqualByComparingTo("2.25");
        assertThat(r.co2EvitadoKg()).isEqualByComparingTo("0");
        assertThat(r.pontos()).isZero();
    }

    @Test
    @DisplayName("Modal mais poluente que a referencia nunca gera CO2 evitado negativo")
    void evitadoNuncaNegativo() {
        ResultadoImpacto r = calculadora.calcular(new BigDecimal("10"), new BigDecimal("200"), REFERENCIA);

        assertThat(r.co2EvitadoKg()).isEqualByComparingTo("0");
        assertThat(r.pontos()).isZero();
    }
}
