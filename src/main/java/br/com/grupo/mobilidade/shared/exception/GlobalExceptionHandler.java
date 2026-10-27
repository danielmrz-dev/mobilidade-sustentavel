package br.com.grupo.mobilidade.shared.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centraliza o tratamento de erros: toda excecao vira um {@link ErroResponse} com o status HTTP correto.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- Erros de negocio ----------

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException ex, HttpServletRequest req) {
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> conflito(ConflitoException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violacao de integridade: {}", ex.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT,
                "A operacao viola uma restricao do banco de dados (registro duplicado ou em uso)", req);
    }

    // ---------- Erros de validacao ----------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErroResponse.CampoInvalido> campos = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                campos.add(new ErroResponse.CampoInvalido(erro.getField(), erro.getDefaultMessage())));
        ex.getBindingResult().getGlobalErrors().forEach(erro ->
                campos.add(new ErroResponse.CampoInvalido(erro.getObjectName(), erro.getDefaultMessage())));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErroResponse.de(status.value(), status.getReasonPhrase(),
                "Existem campos invalidos na requisicao", req.getRequestURI(), campos));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErroResponse> validacaoParametros(HandlerMethodValidationException ex, HttpServletRequest req) {
        String mensagem = ex.getAllErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return responder(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> violacaoRestricao(ConstraintViolationException ex, HttpServletRequest req) {
        String mensagem = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return responder(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String mensagem = "Corpo da requisicao ausente ou com JSON invalido";

        if (ex.getCause() instanceof InvalidFormatException ife) {
            String campo = ife.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                mensagem = "Valor invalido para o campo '" + campo + "'. Valores aceitos: "
                        + Arrays.toString(ife.getTargetType().getEnumConstants());
            } else {
                mensagem = "Valor invalido para o campo '" + campo + "'";
            }
        }
        return responder(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST,
                "Valor '" + ex.getValue() + "' invalido para o parametro '" + ex.getName() + "'", req);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErroResponse> ordenacaoInvalida(PropertyReferenceException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST,
                "Campo de ordenacao invalido: '" + ex.getPropertyName() + "'", req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> parametroAusente(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "Parametro obrigatorio ausente: " + ex.getParameterName(), req);
    }

    // ---------- Seguranca ----------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponse> autenticacao(AuthenticationException ex, HttpServletRequest req) {
        return responder(HttpStatus.UNAUTHORIZED, "E-mail ou senha invalidos", req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> acessoNegado(AccessDeniedException ex, HttpServletRequest req) {
        return responder(HttpStatus.FORBIDDEN, "Voce nao tem permissao para acessar este recurso", req);
    }

    // ---------- HTTP / genericos ----------

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> rotaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, "Rota nao encontrada", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> metodoNaoSuportado(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Metodo " + ex.getMethod() + " nao suportado nesta rota", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> generico(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {}", req.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado. Tente novamente mais tarde.", req);
    }

    private ResponseEntity<ErroResponse> responder(HttpStatus status, String mensagem, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ErroResponse.de(status.value(), status.getReasonPhrase(), mensagem, req.getRequestURI()));
    }
}
