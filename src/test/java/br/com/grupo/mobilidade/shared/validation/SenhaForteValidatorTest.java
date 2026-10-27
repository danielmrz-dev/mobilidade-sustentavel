package br.com.grupo.mobilidade.shared.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SenhaForteValidatorTest {

    private final SenhaForteValidator validator = new SenhaForteValidator();

    @ParameterizedTest
    @ValueSource(strings = {"Senha@123", "Mobilidade#2026", "Abc123!@"})
    void aceitaSenhasFortes(String senha) {
        assertThat(validator.isValid(senha, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"senha", "senha123", "SENHA@123", "Senha123", "Ab1!"})
    void rejeitaSenhasFracas(String senha) {
        assertThat(validator.isValid(senha, null)).isFalse();
    }
}
