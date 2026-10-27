package br.com.grupo.mobilidade.recompensa;

import br.com.grupo.mobilidade.recompensa.dto.RecompensaRequest;
import br.com.grupo.mobilidade.recompensa.dto.RecompensaResponse;
import br.com.grupo.mobilidade.recompensa.dto.ResgateResponse;
import br.com.grupo.mobilidade.shared.exception.ConflitoException;
import br.com.grupo.mobilidade.shared.exception.RecursoNaoEncontradoException;
import br.com.grupo.mobilidade.usuario.Usuario;
import br.com.grupo.mobilidade.usuario.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecompensaService {

    private final RecompensaRepository repository;
    private final ResgateRepository resgateRepository;
    private final UsuarioRepository usuarioRepository;

    public RecompensaService(RecompensaRepository repository, ResgateRepository resgateRepository,
                             UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.resgateRepository = resgateRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Page<RecompensaResponse> listar(boolean somenteDisponiveis, Pageable pageable) {
        Page<Recompensa> pagina = somenteDisponiveis
                ? repository.findByEstoqueGreaterThan(0, pageable)
                : repository.findAll(pageable);
        return pagina.map(RecompensaResponse::from);
    }

    @Transactional(readOnly = true)
    public RecompensaResponse buscar(Long id) {
        return RecompensaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public RecompensaResponse criar(RecompensaRequest request) {
        var recompensa = new Recompensa(request.titulo().trim(), request.descricao(),
                request.custoPontos(), request.estoque());
        return RecompensaResponse.from(repository.save(recompensa));
    }

    @Transactional
    public RecompensaResponse atualizar(Long id, RecompensaRequest request) {
        Recompensa recompensa = buscarEntidade(id);
        recompensa.atualizar(request.titulo().trim(), request.descricao(), request.custoPontos(), request.estoque());
        return RecompensaResponse.from(recompensa);
    }

    @Transactional
    public void remover(Long id) {
        Recompensa recompensa = buscarEntidade(id);
        if (resgateRepository.existsByRecompensaId(id)) {
            throw new ConflitoException(
                    "A recompensa ja foi resgatada e nao pode ser removida. Zere o estoque para retira-la do catalogo.");
        }
        repository.delete(recompensa);
    }

    /**
     * Troca pontos do usuario por uma recompensa: valida estoque e saldo, debita os pontos e registra o resgate.
     */
    @Transactional
    public ResgateResponse resgatar(Long usuarioId, Long recompensaId) {
        Recompensa recompensa = repository.buscarParaResgate(recompensaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recompensa", recompensaId));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", usuarioId));

        recompensa.baixarEstoque();
        usuario.debitarPontos(recompensa.getCustoPontos());

        Resgate resgate = resgateRepository.save(new Resgate(usuario, recompensa, recompensa.getCustoPontos()));
        return ResgateResponse.from(resgate, usuario.getSaldoPontos());
    }

    @Transactional(readOnly = true)
    public Page<ResgateResponse> listarResgatesDoUsuario(Long usuarioId, Pageable pageable) {
        return resgateRepository.findByUsuarioId(usuarioId, pageable)
                .map(resgate -> ResgateResponse.from(resgate, null));
    }

    private Recompensa buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recompensa", id));
    }
}
