package br.com.grupo.mobilidade.recompensa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResgateRepository extends JpaRepository<Resgate, Long> {

    @EntityGraph(attributePaths = "recompensa")
    Page<Resgate> findByUsuarioId(Long usuarioId, Pageable pageable);

    boolean existsByRecompensaId(Long recompensaId);
}
