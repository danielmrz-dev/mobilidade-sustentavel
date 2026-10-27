package br.com.grupo.mobilidade.usuario;

import br.com.grupo.mobilidade.deslocamento.DeslocamentoRepository;
import br.com.grupo.mobilidade.deslocamento.consulta.ResumoImpacto;
import br.com.grupo.mobilidade.shared.exception.RecursoNaoEncontradoException;
import br.com.grupo.mobilidade.usuario.dto.ImpactoUsuarioResponse;
import br.com.grupo.mobilidade.usuario.dto.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final DeslocamentoRepository deslocamentoRepository;

    public UsuarioService(UsuarioRepository repository, DeslocamentoRepository deslocamentoRepository) {
        this.repository = repository;
        this.deslocamentoRepository = deslocamentoRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioResponse perfil(Long usuarioId) {
        return UsuarioResponse.from(buscarEntidade(usuarioId));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(UsuarioResponse::from);
    }

    @Transactional(readOnly = true)
    public ImpactoUsuarioResponse impacto(Long usuarioId) {
        Usuario usuario = buscarEntidade(usuarioId);
        ResumoImpacto resumo = deslocamentoRepository.resumirPorUsuario(usuarioId);
        return new ImpactoUsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                resumo.totalDeslocamentos(),
                resumo.distanciaTotalKm(),
                resumo.co2EmitidoKg(),
                resumo.co2EvitadoKg(),
                resumo.percentualReducao(),
                usuario.getSaldoPontos()
        );
    }

    private Usuario buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", id));
    }
}
