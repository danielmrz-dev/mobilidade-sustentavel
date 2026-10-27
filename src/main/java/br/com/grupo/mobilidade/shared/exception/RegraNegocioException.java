package br.com.grupo.mobilidade.shared.exception;

/**
 * Requisicao valida no formato, mas que viola uma regra de negocio. Vira HTTP 422.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
