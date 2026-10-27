package br.com.grupo.mobilidade.empresa;

import br.com.grupo.mobilidade.deslocamento.DeslocamentoRepository;
import br.com.grupo.mobilidade.empresa.dto.EmpresaRequest;
import br.com.grupo.mobilidade.empresa.dto.EmpresaResponse;
import br.com.grupo.mobilidade.empresa.dto.RankingEmpresaResponse;
import br.com.grupo.mobilidade.shared.exception.ConflitoException;
import br.com.grupo.mobilidade.shared.exception.RecursoNaoEncontradoException;
import br.com.grupo.mobilidade.usuario.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class EmpresaService {

    private static final int TAMANHO_RANKING = 10;

    private final EmpresaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final DeslocamentoRepository deslocamentoRepository;

    public EmpresaService(EmpresaRepository repository, UsuarioRepository usuarioRepository,
                          DeslocamentoRepository deslocamentoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.deslocamentoRepository = deslocamentoRepository;
    }

    @Transactional(readOnly = true)
    public Page<EmpresaResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(EmpresaResponse::from);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscar(Long id) {
        return EmpresaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public EmpresaResponse criar(EmpresaRequest request) {
        if (repository.existsByCnpj(request.cnpj())) {
            throw new ConflitoException("Ja existe uma empresa com o CNPJ " + request.cnpj());
        }
        var empresa = new Empresa(request.nome().trim(), request.cnpj(), request.metaCo2MensalKg());
        return EmpresaResponse.from(repository.save(empresa));
    }

    @Transactional
    public EmpresaResponse atualizar(Long id, EmpresaRequest request) {
        Empresa empresa = buscarEntidade(id);
        if (repository.existsByCnpjAndIdNot(request.cnpj(), id)) {
            throw new ConflitoException("Ja existe outra empresa com o CNPJ " + request.cnpj());
        }
        empresa.atualizar(request.nome().trim(), request.cnpj(), request.metaCo2MensalKg());
        return EmpresaResponse.from(empresa);
    }

    @Transactional
    public void remover(Long id) {
        Empresa empresa = buscarEntidade(id);
        if (usuarioRepository.existsByEmpresaId(id)) {
            throw new ConflitoException("A empresa possui usuarios vinculados e nao pode ser removida");
        }
        repository.delete(empresa);
    }

    /**
     * Progresso do mes atual frente a meta de CO2 evitado e top colaboradores (de todos os tempos).
     */
    @Transactional(readOnly = true)
    public RankingEmpresaResponse ranking(Long id) {
        Empresa empresa = buscarEntidade(id);

        YearMonth mes = YearMonth.now();
        LocalDate inicio = mes.atDay(1);
        LocalDate fim = mes.atEndOfMonth();

        BigDecimal evitadoNoMes = deslocamentoRepository.somarCo2EvitadoDaEmpresa(id, inicio, fim);
        if (evitadoNoMes == null) {
            evitadoNoMes = BigDecimal.ZERO;
        }

        BigDecimal meta = empresa.getMetaCo2MensalKg();
        BigDecimal percentual = null;
        boolean metaAtingida = false;
        if (meta != null && meta.signum() > 0) {
            percentual = evitadoNoMes.multiply(BigDecimal.valueOf(100)).divide(meta, 2, RoundingMode.HALF_UP);
            metaAtingida = evitadoNoMes.compareTo(meta) >= 0;
        }

        var ranking = deslocamentoRepository.rankingUsuariosDaEmpresa(id, PageRequest.of(0, TAMANHO_RANKING));

        return new RankingEmpresaResponse(empresa.getId(), empresa.getNome(), mes.toString(),
                meta, evitadoNoMes, percentual, metaAtingida, ranking);
    }

    public Empresa buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa", id));
    }
}
