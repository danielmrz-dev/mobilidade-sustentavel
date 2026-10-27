package br.com.grupo.mobilidade.deslocamento;

import br.com.grupo.mobilidade.config.MobilidadeProperties;
import br.com.grupo.mobilidade.deslocamento.dto.DeslocamentoRequest;
import br.com.grupo.mobilidade.deslocamento.dto.DeslocamentoResponse;
import br.com.grupo.mobilidade.modal.ModalTransporte;
import br.com.grupo.mobilidade.modal.ModalTransporteRepository;
import br.com.grupo.mobilidade.modal.StatusModal;
import br.com.grupo.mobilidade.modal.TipoModal;
import br.com.grupo.mobilidade.shared.exception.RecursoNaoEncontradoException;
import br.com.grupo.mobilidade.shared.exception.RegraNegocioException;
import br.com.grupo.mobilidade.usuario.Usuario;
import br.com.grupo.mobilidade.usuario.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DeslocamentoService {

    private static final LocalDate INICIO_PADRAO = LocalDate.of(2000, 1, 1);

    private final DeslocamentoRepository repository;
    private final ModalTransporteRepository modalRepository;
    private final UsuarioRepository usuarioRepository;
    private final CalculadoraImpacto calculadora;
    private final MobilidadeProperties properties;

    public DeslocamentoService(DeslocamentoRepository repository,
                               ModalTransporteRepository modalRepository,
                               UsuarioRepository usuarioRepository,
                               CalculadoraImpacto calculadora,
                               MobilidadeProperties properties) {
        this.repository = repository;
        this.modalRepository = modalRepository;
        this.usuarioRepository = usuarioRepository;
        this.calculadora = calculadora;
        this.properties = properties;
    }

    @Transactional
    public DeslocamentoResponse registrar(Long usuarioId, DeslocamentoRequest request) {
        Usuario usuario = buscarUsuario(usuarioId);
        ModalTransporte modal = buscarModalAtivo(request.modalId());

        ResultadoImpacto impacto = calcular(request.distanciaKm(), modal);

        var deslocamento = new Deslocamento(usuario, modal, request.distanciaKm(), request.data(),
                limpar(request.origem()), limpar(request.destino()), impacto);
        repository.save(deslocamento);

        usuario.adicionarPontos(impacto.pontos());
        return DeslocamentoResponse.from(deslocamento);
    }

    @Transactional(readOnly = true)
    public Page<DeslocamentoResponse> listar(Long usuarioId, LocalDate de, LocalDate ate, Pageable pageable) {
        LocalDate inicio = de != null ? de : INICIO_PADRAO;
        LocalDate fim = ate != null ? ate : LocalDate.now();
        if (inicio.isAfter(fim)) {
            throw new RegraNegocioException("A data inicial (de) nao pode ser maior que a data final (ate)");
        }
        return repository.findByUsuarioIdAndDataDeslocamentoBetween(usuarioId, inicio, fim, pageable)
                .map(DeslocamentoResponse::from);
    }

    @Transactional(readOnly = true)
    public DeslocamentoResponse buscar(Long usuarioId, Long id) {
        return DeslocamentoResponse.from(buscarDoUsuario(usuarioId, id));
    }

    /**
     * Recalcula o impacto e ajusta o saldo: estorna os pontos antigos e credita os novos.
     */
    @Transactional
    public DeslocamentoResponse atualizar(Long usuarioId, Long id, DeslocamentoRequest request) {
        Deslocamento deslocamento = buscarDoUsuario(usuarioId, id);
        Usuario usuario = buscarUsuario(usuarioId);

        ModalTransporte modal = deslocamento.getModal().getId().equals(request.modalId())
                ? deslocamento.getModal()          // permite manter um modal que foi inativado depois
                : buscarModalAtivo(request.modalId());

        ResultadoImpacto novoImpacto = calcular(request.distanciaKm(), modal);

        estornarPontos(usuario, deslocamento.getPontosGerados());
        deslocamento.atualizar(modal, request.distanciaKm(), request.data(),
                limpar(request.origem()), limpar(request.destino()), novoImpacto);
        usuario.adicionarPontos(novoImpacto.pontos());

        return DeslocamentoResponse.from(deslocamento);
    }

    @Transactional
    public void remover(Long usuarioId, Long id) {
        Deslocamento deslocamento = buscarDoUsuario(usuarioId, id);
        Usuario usuario = buscarUsuario(usuarioId);

        estornarPontos(usuario, deslocamento.getPontosGerados());
        repository.delete(deslocamento);
    }

    // ------------------------------------------------------------------

    private ResultadoImpacto calcular(BigDecimal distanciaKm, ModalTransporte modal) {
        return calculadora.calcular(distanciaKm, modal.getFatorEmissaoGKm(), fatorReferencia());
    }

    /**
     * Fator do carro a combustao cadastrado no banco; se nao houver, usa o valor padrao da configuracao.
     */
    private BigDecimal fatorReferencia() {
        return modalRepository.findFirstByTipoAndStatusOrderByIdAsc(TipoModal.CARRO_COMBUSTAO, StatusModal.ATIVO)
                .map(ModalTransporte::getFatorEmissaoGKm)
                .orElse(properties.fatorReferenciaPadraoGKm());
    }

    private void estornarPontos(Usuario usuario, int pontos) {
        if (pontos > usuario.getSaldoPontos()) {
            throw new RegraNegocioException(
                    "Os pontos deste deslocamento ja foram usados em resgates, por isso ele nao pode ser alterado ou removido");
        }
        usuario.debitarPontos(pontos);
    }

    private Deslocamento buscarDoUsuario(Long usuarioId, Long id) {
        // Se o deslocamento for de outro usuario, responde 404 para nao revelar que ele existe
        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Deslocamento", id));
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", usuarioId));
    }

    private ModalTransporte buscarModalAtivo(Long modalId) {
        ModalTransporte modal = modalRepository.findById(modalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Modal", modalId));
        if (!modal.isAtivo()) {
            throw new RegraNegocioException("O modal '" + modal.getNome() + "' esta inativo e nao aceita novos registros");
        }
        return modal;
    }

    private static String limpar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
