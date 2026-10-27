package br.com.grupo.mobilidade.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato padrao de erro devolvido por toda a API.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoInvalido> campos
) {

    public record CampoInvalido(String campo, String mensagem) {
    }

    public static ErroResponse de(int status, String erro, String mensagem, String caminho) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem, caminho, List.of());
    }

    public static ErroResponse de(int status, String erro, String mensagem, String caminho, List<CampoInvalido> campos) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem, caminho, campos);
    }
}
