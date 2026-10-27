package br.com.grupo.mobilidade.auth.dto;

import br.com.grupo.mobilidade.shared.validation.SenhaForte;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegistroRequest(

        @NotBlank(message = "O nome e obrigatorio")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "O e-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        @Size(max = 150, message = "O e-mail deve ter no maximo 150 caracteres")
        String email,

        @NotBlank(message = "A senha e obrigatoria")
        @SenhaForte
        String senha,

        @Positive(message = "Id de empresa invalido")
        Long empresaId
) {
}
