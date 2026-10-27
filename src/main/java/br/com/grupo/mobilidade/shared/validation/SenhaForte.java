package br.com.grupo.mobilidade.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validacao customizada: senha com no minimo 8 caracteres,
 * contendo letra maiuscula, letra minuscula, numero e caractere especial.
 */
@Documented
@Constraint(validatedBy = SenhaForteValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface SenhaForte {

    String message() default "A senha deve ter no minimo 8 caracteres, com letra maiuscula, letra minuscula, numero e caractere especial";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
