package br.com.grupo.mobilidade.shared.exception;

/**
 * Conflito com o estado atual dos dados (ex.: e-mail ou CNPJ ja cadastrado). Vira HTTP 409.
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
