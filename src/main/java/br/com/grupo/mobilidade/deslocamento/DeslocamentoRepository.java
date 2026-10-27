package br.com.grupo.mobilidade.deslocamento;

import br.com.grupo.mobilidade.deslocamento.consulta.ImpactoPorModal;
import br.com.grupo.mobilidade.deslocamento.consulta.RankingEmpresa;
import br.com.grupo.mobilidade.deslocamento.consulta.RankingUsuario;
import br.com.grupo.mobilidade.deslocamento.consulta.ResumoImpacto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeslocamentoRepository extends JpaRepository<Deslocamento, Long> {

    @EntityGraph(attributePaths = "modal")
    Page<Deslocamento> findByUsuarioIdAndDataDeslocamentoBetween(Long usuarioId, LocalDate inicio, LocalDate fim,
                                                                 Pageable pageable);

    @EntityGraph(attributePaths = "modal")
    Optional<Deslocamento> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Query("""
            select new br.com.grupo.mobilidade.deslocamento.consulta.ResumoImpacto(
                count(d), sum(d.distanciaKm), sum(d.co2EmitidoKg), sum(d.co2EvitadoKg))
            from Deslocamento d
            where d.usuario.id = :usuarioId
            """)
    ResumoImpacto resumirPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
            select new br.com.grupo.mobilidade.deslocamento.consulta.ResumoImpacto(
                count(d), sum(d.distanciaKm), sum(d.co2EmitidoKg), sum(d.co2EvitadoKg))
            from Deslocamento d
            """)
    ResumoImpacto resumirGeral();

    @Query("""
            select new br.com.grupo.mobilidade.deslocamento.consulta.ImpactoPorModal(
                m.nome, m.tipo, count(d), sum(d.distanciaKm), sum(d.co2EvitadoKg))
            from Deslocamento d join d.modal m
            group by m.nome, m.tipo
            order by sum(d.co2EvitadoKg) desc
            """)
    List<ImpactoPorModal> impactoPorModal();

    @Query("""
            select new br.com.grupo.mobilidade.deslocamento.consulta.RankingUsuario(
                u.id, u.nome, count(d), sum(d.co2EvitadoKg))
            from Deslocamento d join d.usuario u
            where u.empresa.id = :empresaId
            group by u.id, u.nome
            order by sum(d.co2EvitadoKg) desc
            """)
    List<RankingUsuario> rankingUsuariosDaEmpresa(@Param("empresaId") Long empresaId, Pageable pageable);

    @Query("""
            select new br.com.grupo.mobilidade.deslocamento.consulta.RankingEmpresa(
                e.id, e.nome, count(d), sum(d.co2EvitadoKg))
            from Deslocamento d join d.usuario u join u.empresa e
            group by e.id, e.nome
            order by sum(d.co2EvitadoKg) desc
            """)
    List<RankingEmpresa> rankingEmpresas(Pageable pageable);

    @Query("""
            select sum(d.co2EvitadoKg)
            from Deslocamento d
            where d.usuario.empresa.id = :empresaId
              and d.dataDeslocamento between :inicio and :fim
            """)
    BigDecimal somarCo2EvitadoDaEmpresa(@Param("empresaId") Long empresaId,
                                        @Param("inicio") LocalDate inicio,
                                        @Param("fim") LocalDate fim);
}
