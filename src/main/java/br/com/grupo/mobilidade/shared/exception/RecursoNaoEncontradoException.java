package br.com.grupo.mobilidade.shared.exception;

/**
 * Recurso inexistente (ou que nao pertence ao usuario logado). Vira HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " nao encontrado(a)");
    }

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
