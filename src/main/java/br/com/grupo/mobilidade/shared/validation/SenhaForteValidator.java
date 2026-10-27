package br.com.grupo.mobilidade.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SenhaForteValidator implements ConstraintValidator<SenhaForte, String> {

    private static final Pattern REGRA = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,60}$");

    @Override
    public boolean isValid(String senha, ConstraintValidatorContext context) {
        // Valor nulo e responsabilidade do @NotBlank
        if (senha == null) {
            return true;
        }
        return REGRA.matcher(senha).matches();
    }
}
