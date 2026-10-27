package br.com.grupo.mobilidade.modal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModalTransporteRepository extends JpaRepository<ModalTransporte, Long> {

    Page<ModalTransporte> findByStatus(StatusModal status, Pageable pageable);

    Optional<ModalTransporte> findFirstByTipoAndStatusOrderByIdAsc(TipoModal tipo, StatusModal status);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
