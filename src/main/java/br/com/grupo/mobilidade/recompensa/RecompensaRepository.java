package br.com.grupo.mobilidade.recompensa;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {

    Page<Recompensa> findByEstoqueGreaterThan(Integer estoque, Pageable pageable);

    /**
     * SELECT ... FOR UPDATE: evita que dois resgates simultaneos consumam a ultima unidade do estoque.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Recompensa r where r.id = :id")
    Optional<Recompensa> buscarParaResgate(@Param("id") Long id);
}
