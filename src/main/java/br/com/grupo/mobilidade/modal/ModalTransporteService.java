package br.com.grupo.mobilidade.modal;

import br.com.grupo.mobilidade.modal.dto.ModalRequest;
import br.com.grupo.mobilidade.modal.dto.ModalResponse;
import br.com.grupo.mobilidade.shared.exception.ConflitoException;
import br.com.grupo.mobilidade.shared.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModalTransporteService {

    private final ModalTransporteRepository repository;

    public ModalTransporteService(ModalTransporteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<ModalResponse> listar(StatusModal status, Pageable pageable) {
        Page<ModalTransporte> pagina = status == null
                ? repository.findAll(pageable)
                : repository.findByStatus(status, pageable);
        return pagina.map(ModalResponse::from);
    }

    @Transactional(readOnly = true)
    public ModalResponse buscar(Long id) {
        return ModalResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ModalResponse criar(ModalRequest request) {
        if (repository.existsByNomeIgnoreCase(request.nome())) {
            throw new ConflitoException("Ja existe um modal com o nome '" + request.nome() + "'");
        }
        var modal = new ModalTransporte(request.nome().trim(), request.tipo(), request.fatorEmissaoGKm());
        return ModalResponse.from(repository.save(modal));
    }

    @Transactional
    public ModalResponse atualizar(Long id, ModalRequest request) {
        var modal = buscarEntidade(id);
        if (repository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
            throw new ConflitoException("Ja existe um modal com o nome '" + request.nome() + "'");
        }
        modal.atualizar(request.nome().trim(), request.tipo(), request.fatorEmissaoGKm());
        return ModalResponse.from(modal);
    }

    /**
     * Exclusao logica: o modal fica INATIVO para preservar o historico de deslocamentos.
     */
    @Transactional
    public void inativar(Long id) {
        buscarEntidade(id).inativar();
    }

    public ModalTransporte buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Modal", id));
    }
}
